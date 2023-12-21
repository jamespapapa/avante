package com.avante.application.usecase.mapper

import com.avante.adapter.input.http.member.dto.request.SignUpRequest
import com.avante.domain.member.Member
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class MemberMapper {
    @Mappings(
        Mapping(ignore = true, target = "passwd"/*, qualifiedByName = ["encodePasswd"]*/)
    )
    abstract fun signUpRequestToEntity(signUpRequest: SignUpRequest): Member
}
