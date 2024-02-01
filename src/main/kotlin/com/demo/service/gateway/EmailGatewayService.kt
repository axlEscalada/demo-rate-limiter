package com.demo.service.gateway

import com.demo.dto.NotificationPayloadDto
import com.demo.service.RateLimiterService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EmailGatewayService: NotificationGatewayService{

    var logger: Logger = LoggerFactory.getLogger(EmailGatewayService::class.java)

    override fun send(payloadDto: NotificationPayloadDto) {
        logger.info("Notification sent to ${payloadDto.recipientAddress}")
        //Implement a email service
    }
}
