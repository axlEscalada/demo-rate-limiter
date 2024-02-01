package com.demo

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NotificationServiceApplication

	fun main(args: Array<String>) {
		runApplication<NotificationServiceApplication>(*args)
	}
