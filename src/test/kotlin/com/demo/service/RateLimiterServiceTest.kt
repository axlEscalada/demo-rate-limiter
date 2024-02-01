package com.demo.service

import com.demo.configuration.DomainConfiguration
import com.demo.configuration.DomainType
import com.demo.configuration.RateLimiterConfiguration
import com.demo.dto.NotificationPayloadDto
import com.demo.exception.RateLimiterExceededException
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.util.Maps
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.data.redis.core.script.RedisScript
import java.util.concurrent.TimeUnit

@ExtendWith(MockKExtension::class)
class RateLimiterServiceTest {

    companion object {
        val LIMIT = 2
    }

    @MockK
    lateinit var rateLimiterConfiguration: RateLimiterConfiguration

    @MockK
    lateinit var redisTemplate: RedisTemplate<String, Int>

    @MockK
    lateinit var valueOps: ValueOperations<String, Int>

    @InjectMockKs
    lateinit var rateLimiterService: RateLimiterService

    @BeforeEach
    fun init() {
        MockKAnnotations.init(this)
        every { redisTemplate.opsForValue() } returns valueOps
        every {
            rateLimiterConfiguration.configMap
        } returns Maps.newHashMap(DomainType.MARKETING, DomainConfiguration(UnitType.MINUTES, 1, LIMIT))
    }

    @Test
    @DisplayName("Should save first notification in redis with a ttl")
    fun shouldSaveInRedisNotificationNumer() {
        // Given
        every {
            valueOps.get(any())
        } returns null
        every {
            valueOps.set(any(), any(), any(), any())
        } just runs

        // When
        var payload = NotificationPayloadDto(DomainType.MARKETING, "fakemail@gmail.com", "Email message")
        rateLimiterService.checkRateLimit(payload)

        // Then
        verify(exactly = 1) {
            valueOps.set("MARKETING-fakemail@gmail.com", 1, 1, TimeUnit.MINUTES)
        }
    }


    @Test
    @DisplayName("Should save a second notification in redis keeping ttl from first one")
    fun shouldSaveTwoNotificationTries() {
        // Given
        every {
            valueOps.get(any())
        } returnsMany listOf(null, 1)
        every {
            valueOps.set(any(), any(), any(), any())
        } just runs
        every {
            redisTemplate.execute(any(RedisScript::class), any(), any())
        } answers { nothing }

        // When
        var payload = NotificationPayloadDto(DomainType.MARKETING, "fakemail@gmail.com", "Email message")
        rateLimiterService.checkRateLimit(payload)
        rateLimiterService.checkRateLimit(payload)

        // Then
        verify(exactly = 1) {
            valueOps.set("MARKETING-fakemail@gmail.com", 1, 1, TimeUnit.MINUTES)
        }

        verify(exactly = 1) {
            redisTemplate.execute(any(RedisScript::class), any(), any())
        }
    }

    @Test
    @DisplayName("Should throw an rate limit exceed exception when reach limit")
    fun shouldThrowExceptionWhenReachLimit() {
        // Given
        every {
            valueOps.get(any())
        } returns LIMIT

        // When/Then
        var payload = NotificationPayloadDto(DomainType.MARKETING, "fakemail@gmail.com", "Email message")
        assertThrows<RateLimiterExceededException> { rateLimiterService.checkRateLimit(payload) }
    }
}
