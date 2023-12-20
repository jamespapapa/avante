package com.avante.application.port.output

import com.avante.domain.member.Member

interface MemberOutputPort {
    fun saveMember(member: Member): Member
    fun findMemberByIdOrNull(id: String): Member?
}
