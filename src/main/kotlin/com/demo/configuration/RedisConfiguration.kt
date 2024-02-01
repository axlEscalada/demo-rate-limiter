package com.demo.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import redis.clients.jedis.JedisPoolConfig




@Configuration
class RedisConfiguration(
    @Value("\${spring.redis.host}")
    val host: String,
    @Value("\${spring.redis.port}")
    val port: Int) {

    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory? {
        var config = RedisStandaloneConfiguration()
        config.hostName = host
        config.port = port
        return JedisConnectionFactory(config)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Int>? {
        val template = RedisTemplate<String, Int>()
        template.valueSerializer = GenericJackson2JsonRedisSerializer()
        template.keySerializer = StringRedisSerializer()
        template.connectionFactory = jedisConnectionFactory()
        return template
    }

}
