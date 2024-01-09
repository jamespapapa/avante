package com.avante.application.port.output

import com.avante.domain.member.Member
import org.springframework.data.jpa.repository.JpaRepository

// TODO MemberRepository 역할을 하는 것 같은데 헥사고날 아키텍쳐의 전체적인 이해 필요(UseCase(Service) 등)
interface MemberOutputPort : JpaRepository<Member, String>
