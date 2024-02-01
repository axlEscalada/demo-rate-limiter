package com.demo.service

import com.demo.configuration.DomainConfiguration
import com.demo.configuration.RateLimiterConfiguration
import com.demo.dto.NotificationPayloadDto
import com.demo.exception.NotFoundConfigurationException
import com.demo.exception.RateLimiterExceededException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Service
import java.util.Collections
import java.util.concurrent.TimeUnit

@Service
class RateLimiterService(
    @Autowired
    val rateLimiterConfiguration: RateLimiterConfiguration,
    @Autowired
    val redisTemplate: RedisTemplate<String, Int>
){

    var logger: Logger = LoggerFactory.getLogger(RateLimiterService::class.java)

    fun checkRateLimit(payload: NotificationPayloadDto) {
        val key = buildKey(payload)
        val value = redisTemplate.opsForValue().get(buildKey(payload))?: 0
        val configByType = rateLimiterConfiguration.getConfigurationByDomain(payload.domainType.name.lowercase())

        if(configByType.limit <= value) {
            logger.error("Limit of ${configByType.limit} notifications exceeded for address ${payload.recipientAddress} " +
                    "in notification domain ${payload.domainType.name}")
            throw RateLimiterExceededException("Limit exceeded for ${payload.recipientAddress} in ${payload.domainType.name} domain")
        }

        setEntry(key, value, configByType)
    }

    private fun setEntry(
        key: String,
        existingMessage: Int,
        configByType: DomainConfiguration
    ) {
        var updatedValue = existingMessage.plus(1)
        if (existingMessage == 0) {
            redisTemplate.opsForValue()
                .set(key, updatedValue, configByType.rate.toLong(), configByType.unit)
        } else {
            val keepTtlScript = RedisScript.of<Any>("return redis.call('SET', KEYS[1], ARGV[1], 'KEEPTTL')")
            redisTemplate.execute(keepTtlScript, Collections.singletonList(key), updatedValue)
        }
    }

    private fun buildKey(payload: NotificationPayloadDto): String {
        return payload.domainType.name.plus("-").plus(payload.recipientAddress)
    }

}
