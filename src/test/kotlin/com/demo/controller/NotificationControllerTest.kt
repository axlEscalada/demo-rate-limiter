package com.demo.controller

import com.demo.configuration.DomainType
import com.demo.dto.NotificationPayloadDto
import com.demo.service.NotificationService
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockKExtension::class)
class NotificationControllerTest {

    val mapper: ObjectMapper = ObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true)
        propertyNamingStrategy = PropertyNamingStrategies.LOWER_CAMEL_CASE
    }
    private lateinit var mockMvc: MockMvc

    @MockK
    lateinit var notificationService: NotificationService

    @BeforeEach
    fun init() {
        val controller = NotificationController(notificationService)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    @Throws(Exception::class)
    @DisplayName("Should take the request and call notification service")
    fun shouldTakeRequestAndProcessNotificationPayload() {
        // Given
        every { notificationService.sendNotification(any()) } just runs

        // When
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/v1/notify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(asJsonString(NotificationPayloadDto(DomainType.MARKETING, "fakeemail@gmail.com", "A message")))
            )
            .andDo(MockMvcResultHandlers.print())
            // Then
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    fun asJsonString(obj: Any?): String {
        return try {
            mapper.writeValueAsString(obj)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
