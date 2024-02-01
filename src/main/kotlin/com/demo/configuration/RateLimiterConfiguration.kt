package com.demo.configuration

import com.demo.exception.NotFoundConfigurationException
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "limits")
class RateLimiterConfiguration {
    var configMap: Map<String, DomainConfiguration>? = null

    fun getConfigurationByDomain(domain: String): DomainConfiguration {
        val ex = NotFoundConfigurationException("There is no configuration for $domain")
        return configMap?.getOrThrow(domain, ex) ?: throw ex
    }

    private fun <T, V> Map<T, V>.getOrThrow(key: T, ex: Throwable): V {
        return this.get(key) ?: throw ex
    }
}
