package com.avante.adapter.input.http.hello

import com.avante.adapter.input.http.hello.dto.request.Greeting
import com.avante.adapter.input.http.member.dto.request.LoginRequest
import com.avante.adapter.input.http.member.dto.request.SignUpRequest
import com.avante.common.dto.CommonResponse
import com.avante.common.dto.JwtToken
import com.avante.common.util.JwtUtil
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
@AutoConfigureMockMvc
class MemberControllerTest(
    val mockMvc: MockMvc
) {
    var guest: JwtToken? = null
    var user: JwtToken? = null

    @BeforeEach
    fun setUp() {
        if (guest == null) {
            guest = JwtUtil.generateToken("avante", listOf("GUEST"))
            user = JwtUtil.generateToken("avanteAuthorized", listOf("USER"))
        }
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    @DisplayName("sign-up")
    fun signUpTest() {
        val request = SignUpRequest("jules.my", "julespasswd", "jules")

        val jsonBody = jacksonObjectMapper().writeValueAsString(request)

        mockMvc.perform(
            post("/members/v1/sign-up")
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
    @DisplayName("회원 가입 이후 로그인에 성공한다")
    fun `회원가입 이후 로그인에 성공한다`() {
        val request = SignUpRequest("jules.my", "julespasswd", "jules")

        val jsonBody = jacksonObjectMapper().writeValueAsString(request)

        mockMvc.perform(
            post("/members/v1/sign-up")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(
                status().isOk
            )
            .andDo(MockMvcResultHandlers.print())

        val loginSuccess = LoginRequest("jules.my", "julespasswd")
        mockMvc.perform(
            post("/members/v1/login")
                .content(jacksonObjectMapper().writeValueAsString(loginSuccess))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(
                status().isBadRequest
            )
            .andDo(MockMvcResultHandlers.print())

        val wrongPasswd = LoginRequest("jules.my", "julespasswd222")
        mockMvc.perform(
            post("/members/v1/login")
                .content(jacksonObjectMapper().writeValueAsString(wrongPasswd))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(
                status().isBadRequest
            )
            .andDo(MockMvcResultHandlers.print())

        val nonExistingId = LoginRequest("a.b", "passwd")
        mockMvc.perform(
            post("/members/v1/login")
                .content(jacksonObjectMapper().writeValueAsString(nonExistingId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(
                status().isBadRequest
            )
            .andDo(MockMvcResultHandlers.print())

        val invalidRequest = LoginRequest("", "")
        mockMvc.perform(
            post("/members/v1/login")
                .content(jacksonObjectMapper().writeValueAsString(invalidRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(
                status().isBadRequest
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    @DisplayName("회원가입 이후 받은 토큰으로 인증이 필요한 api 호출 성공")
    fun `회원가입 이후 받은 토큰으로 인증이 필요한 api 호출 성공`() {
        val request = SignUpRequest("jules.my", "julespasswd", "jules")

        val jsonBody = jacksonObjectMapper().writeValueAsString(request)

        val response = mockMvc.perform(
            post("/members/v1/sign-up")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(
                status().isOk
            )
            .andReturn().response.contentAsString

        val token = jacksonObjectMapper().readValue(response, CommonResponse::class.java).data as Map<String, Any>

        val helloRequest = jacksonObjectMapper().writeValueAsString(Greeting("Hello !"))

        mockMvc.perform(
            post("/hello")
                .content(helloRequest)
                .header("Authorization", "Bearer ${token["accessToken"]}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(
                status().isOk
            )
            .andDo(MockMvcResultHandlers.print())
    }
}
