package com.demo.controller

import com.demo.dto.NotificationPayloadDto
import com.demo.service.NotificationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class NotificationController(val notificationService: NotificationService){

    @PostMapping("/notify")
    fun sendNotification(@RequestBody payload: NotificationPayloadDto) {
        notificationService.sendNotification(payload)
    }
}
