package com.demo.integration

import com.demo.configuration.DomainType
import com.demo.dto.NotificationPayloadDto
import com.demo.exception.RateLimiterExceededException
import com.demo.service.RateLimiterService
import com.redis.testcontainers.RedisContainer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@ExtendWith(SpringExtension::class)
@SpringBootTest
internal class RateLimiterIntegrationTest {

    companion object {
        @Container
        private val redisContainer = RedisContainer(DockerImageName.parse("redis:latest"))

        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("limits.configmap.status.unit") {"SECONDS"}
            registry.add("limits.configmap.status.rate") {"2"}
            registry.add("limits.configmap.status.limit") {"2"}
            registry.add("spring.data.redis.repositories.enabled") { true }
            registry.add("spring.redis.host") { redisContainer.host }
            registry.add("spring.redis.port") { redisContainer.firstMappedPort }
        }
    }

    @Autowired
    lateinit var rateLimiter: RateLimiterService

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, Int>

    @BeforeEach
    fun cleanDb() {
        redisTemplate.getConnectionFactory()!!.getConnection().flushAll();
    }

    @Test
    @DisplayName("Rate limiter should drop third message if its inside window of 2 seconds of first message")
    fun testRateLimiter() {
        var payloadDto = NotificationPayloadDto(DomainType.STATUS, "fakemail@gmail.com", "A message")
        rateLimiter.checkRateLimit(payloadDto)
        rateLimiter.checkRateLimit(payloadDto)

        assertThrows<RateLimiterExceededException>() { rateLimiter.checkRateLimit(payloadDto) }
    }

    @Test
    @DisplayName("Rate limiter should drop third message if its inside window of 2 seconds of first message and let notify a new message after 2 seconds")
    fun shouldDropThirdNotificationAndLetSendLastNotificationAfter2SecondWindow() {
        var payloadDto = NotificationPayloadDto(DomainType.STATUS, "fakemail@gmail.com", "A message")
        rateLimiter.checkRateLimit(payloadDto)
        rateLimiter.checkRateLimit(payloadDto)

        assertThrows<RateLimiterExceededException>() { rateLimiter.checkRateLimit(payloadDto) }

        Thread.sleep(2000)

        rateLimiter.checkRateLimit(payloadDto)
    }
}
