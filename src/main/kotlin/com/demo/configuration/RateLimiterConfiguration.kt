package com.demo.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "limits")
class RateLimiterConfiguration {
    var configMap: Map<String, DomainConfiguration>? = null
}
