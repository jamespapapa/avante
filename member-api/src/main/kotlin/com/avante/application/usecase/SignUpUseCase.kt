package com.avante.application.usecase

import com.avante.adapter.input.http.member.dto.request.LoginRequest
import com.avante.adapter.input.http.member.dto.request.SignUpRequest
import com.avante.adapter.output.persistence.MemberRepository
import com.avante.application.usecase.mapper.MemberMapper
import com.avante.common.dto.JwtToken
import com.avante.common.exception.CommonBadRequestException
import com.avante.common.util.JwtUtil
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SignUpUseCase(
//    private val memberOutputPort: MemberOutputPort,
    private val memberRepository: MemberRepository,
    private val memberMapper: MemberMapper,
    private val passwordEncoder: BCryptPasswordEncoder,
) {

    @Transactional
    fun signUp(request: SignUpRequest): JwtToken {
        val member = memberMapper.signUpRequestToEntity(request)
        member.roles = listOf("ROLE_USER", "ROLE_DEFAULT")
        val saved = memberRepository.save(member)

        val user = User(saved.id, saved.password, saved.authorities)

        return JwtUtil.generateToken(UsernamePasswordAuthenticationToken(user, saved.password, saved.authorities))
    }

    @Transactional(readOnly = true)
    fun login(request: LoginRequest): JwtToken {
        val passwordEncoded = passwordEncoder.encode(request.passwd)
        val found = memberRepository.findByIdOrNull(request.id)?: throw CommonBadRequestException("Login failed.")

        if(passwordEncoded != found.password) throw CommonBadRequestException("Login failed.")

        val user = User(found.id, found.password, found.authorities)

        return JwtUtil.generateToken(UsernamePasswordAuthenticationToken(user, found.password, found.authorities))
    }
}
