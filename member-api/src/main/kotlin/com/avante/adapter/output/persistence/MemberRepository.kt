package com.avante.adapter.output.persistence

import com.avante.application.port.output.MemberOutputPort
import org.springframework.stereotype.Repository

// TODO 추후 CustomRepository 역할을 위한 추상 클래스인지 파악
@Repository
abstract class MemberRepository : MemberOutputPort
