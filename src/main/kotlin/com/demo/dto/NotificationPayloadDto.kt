package com.demo.dto

import com.demo.configuration.DomainType
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class NotificationPayloadDto (
    @field:NotNull val domainType: DomainType,
    @field:NotEmpty val recipientAddress: String,
    @field:NotEmpty val message: String,
)
