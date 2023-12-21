package com.avante.application.usecase

import com.avante.adapter.input.http.member.dto.request.SignUpRequest
import com.avante.application.port.output.MemberOutputPort
import com.avante.application.usecase.mapper.MemberMapper
import com.avante.domain.member.QMember
import com.querydsl.jpa.impl.JPAQueryFactory
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
class SignUpUseCaseTest(
    val memberOutputPort: MemberOutputPort,
    val memberMapper: MemberMapper,
    val queryFactory: JPAQueryFactory
) {

    @Test
    fun queryDslQuerySuccess() {
        val memberCreateRequest = SignUpRequest("jules.my", "1234", "jules")

        val m = memberMapper.signUpRequestToEntity(memberCreateRequest)

        memberOutputPort.save(m)

        val queryResult = queryFactory.selectFrom(QMember.member)
            .where(QMember.member.id.eq("jules.my"))
            .fetchOne()

        assertNotNull(queryResult)
    }
}
