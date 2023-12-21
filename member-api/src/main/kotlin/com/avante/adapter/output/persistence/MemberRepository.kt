package com.avante.adapter.output.persistence

import com.avante.application.port.output.MemberOutputPort
import org.springframework.stereotype.Repository

@Repository
abstract class MemberRepository : MemberOutputPort
