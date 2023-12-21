package com.avante.application.port.output

import com.avante.domain.member.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberOutputPort : JpaRepository<Member, String>
