package com.avante.application.usecase.mapper

import com.avante.adapter.input.http.member.dto.request.LoginRequest
import com.avante.adapter.input.http.member.dto.request.SignUpRequest
import com.avante.domain.member.Member
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.Named
import org.mapstruct.ReportingPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class MemberMapper(
    private val passwordEncoder: BCryptPasswordEncoder
) {
    @Mappings(
        Mapping(source = "passwd", target = "passwd", qualifiedByName = ["encodePasswd"]),
    )
    abstract fun signUpRequestToEntity(signUpRequest: SignUpRequest): Member

    @Named("encodePasswd")
    fun encodePasswd(source: String): String {
        return passwordEncoder.encode(source)
    }
}
