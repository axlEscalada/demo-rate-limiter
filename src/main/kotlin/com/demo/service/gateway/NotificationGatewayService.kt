package com.demo.service.gateway

import com.demo.dto.NotificationPayloadDto

interface NotificationGatewayService {
    fun send(payloadDto: NotificationPayloadDto)
}
