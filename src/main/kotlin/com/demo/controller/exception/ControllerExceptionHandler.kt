package com.demo.controller.exception

import com.demo.exception.RateLimiterExceededException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ControllerExceptionHandler: ResponseEntityExceptionHandler() {

    @ExceptionHandler(RateLimiterExceededException::class)
    fun handleRateLimitException(exception: RateLimiterExceededException, request: WebRequest): ResponseEntity<Any> {
        return handleExceptionInternal(
            exception,
            ErrorResponseDto(exception.message!!),
            HttpHeaders(),
            HttpStatus.CONFLICT,
            request
        )!!
    }

}
