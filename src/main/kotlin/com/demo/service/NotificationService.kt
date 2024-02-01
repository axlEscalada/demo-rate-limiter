package com.demo.service

import com.demo.dto.NotificationPayloadDto
import com.demo.service.gateway.NotificationGatewayService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class NotificationService(
    @Autowired val rateLimiterService: RateLimiterService,
    @Autowired val notificationGateway: NotificationGatewayService
){

    fun sendNotification(payload: NotificationPayloadDto) {
        rateLimiterService.checkRateLimit(payload)
        notificationGateway.send(payload)
    }
}
