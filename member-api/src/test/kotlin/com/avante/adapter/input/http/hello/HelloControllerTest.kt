package com.avante.adapter.input.http.hello

import com.avante.adapter.input.http.hello.dto.request.Greeting
import com.avante.common.dto.JwtToken
import com.avante.common.util.JwtUtil
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
    var guest: JwtToken? = null
    var user: JwtToken? = null

    @BeforeEach
    fun setUp() {
        if(guest == null) {
            guest = JwtUtil.generateToken("avante", listOf("ROLE_GUEST"))
            user = JwtUtil.generateToken("avanteAuthorized", listOf("ROLE_USER"))
        }
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    @DisplayName("Greeting Without Token")
    fun greetingUnknown() {
        val request = Greeting("Hello !")

        val jsonBody = jacksonObjectMapper().writeValueAsString(request)

        mockMvc.perform(
            post("/hello")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(
                status().isUnauthorized
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("Greeting by Guest")
    fun greetingGuest() {
        val request = Greeting("Hello !")

        val jsonBody = jacksonObjectMapper().writeValueAsString(request)

        mockMvc.perform(
            post("/hello")
                .header("Authorization", "Bearer ${guest?.accessToken}")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(
                status().isForbidden
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("Greeting user")
    fun `Greeting 성공`() {
        /** Not null parameter message */
        val request = Greeting("Hello~!")

        val jsonBody = jacksonObjectMapper().writeValueAsString(request)

        mockMvc.perform(
            post("/hello")
                .header("Authorization", "Bearer ${user?.accessToken}")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(
                status().isOk
            )
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
                .header("Authorization", "Bearer ${user?.accessToken}")
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
