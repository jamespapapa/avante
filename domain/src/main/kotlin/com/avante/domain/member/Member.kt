package com.avante.domain.member

import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "member")
@BatchSize(size = 200)
class Member : UserDetails {

    // TODO 보통 id(pk)는 Long 값과 같은 생성값으로 가져가는 것이 일반적인데 String(전화번호?)로 가져가도 문제가 없을지
    @Id
    @Column(name = "id", updatable = false, unique = true, nullable = false)
    var id: String = ""

    @Column(name = "passwd", nullable = false)
    var passwd: String = ""

    @Column(name = "member_name", nullable = false)
    var name: String = ""

    @ElementCollection(fetch = FetchType.LAZY)
    var roles: List<String> = mutableListOf()

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> { // TODO out 키워드 파악
        return roles.map { SimpleGrantedAuthority(it) }.toMutableList()
    }

    override fun getPassword(): String {
        return passwd
    }

    override fun getUsername(): String {
        return id
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
