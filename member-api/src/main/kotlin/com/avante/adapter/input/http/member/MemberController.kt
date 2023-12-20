package com.avante.adapter.input.http.member

import com.avante.adapter.input.http.member.dto.request.LoginRequest
import com.avante.adapter.input.http.member.dto.request.SignUpRequest
import com.avante.application.usecase.SignUpUseCase
import com.avante.common.dto.CommonResponse
import com.avante.common.dto.JwtToken
import com.avante.domain.member.Member
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
@RequestMapping("/members")
class MemberController(
    val signUpUseCase: SignUpUseCase
) {

    @PostMapping("/sign-up")
    fun signUp(@Valid @RequestBody request: SignUpRequest): CommonResponse<JwtToken> {
        return CommonResponse(HttpStatus.OK.value(), HttpStatus.OK.reasonPhrase, signUpUseCase.signUp(request))
    }
    @PostMapping("/login")
    fun signUp(@Valid @RequestBody request: LoginRequest): CommonResponse<JwtToken> {
        return CommonResponse(HttpStatus.OK.value(), HttpStatus.OK.reasonPhrase, signUpUseCase.login(request))
    }

}
