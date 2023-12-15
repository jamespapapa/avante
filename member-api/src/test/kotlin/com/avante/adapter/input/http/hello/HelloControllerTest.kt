package com.avante.adapter.input.http.hello

import com.avante.adapter.input.http.hello.dto.request.Greeting
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.hamcrest.core.StringContains.containsString
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
@AutoConfigureMockMvc
class HelloControllerTest(
    val mockMvc: MockMvc
) {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    @DisplayName("Greeting")
    fun greeting() {
        val request = Greeting("Hello !")

        val jsonBody = jacksonObjectMapper().writeValueAsString(request)

        mockMvc.perform(
            post("/hello")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(
                status().isOk
            )
            .andExpect(jsonPath("$.my_feeling", containsString("GooD")))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("스프링 validation이 정상 동작 한다")
    fun `스프링 validation이 정상 동작 한다`() {
        /** Not null parameter message */
        val request = Greeting(null)

        val jsonBody = jacksonObjectMapper().writeValueAsString(request)

        mockMvc.perform(
            post("/hello")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(
                status().isBadRequest
            )
            .andExpect(jsonPath("$.errors[0].fieldName", containsString("message")))
            .andDo(MockMvcResultHandlers.print())
    }
}
