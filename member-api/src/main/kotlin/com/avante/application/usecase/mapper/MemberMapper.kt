package com.avante.application.usecase.mapper

import com.avante.adapter.input.http.member.dto.request.SignUpRequest
import com.avante.domain.member.Member
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.ReportingPolicy

// TODO Entity로 변환하는데 쓰이는 Mapper로 보이는데 사용해본적이 없어서 파악 필요
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class MemberMapper {
    @Mappings(
        Mapping(ignore = true, target = "passwd"/*, qualifiedByName = ["encodePasswd"]*/)
    )
    abstract fun signUpRequestToEntity(signUpRequest: SignUpRequest): Member
}
