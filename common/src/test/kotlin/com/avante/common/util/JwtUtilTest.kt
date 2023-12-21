package com.avante.common.util

import com.avante.common.exception.CommonUnauthorizedException
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

class JwtUtilTest {

    @Test
    fun `jwt 정상 생성`() {
        val authorities = listOf(SimpleGrantedAuthority("GUEST"))
        val user = User("avante", "", authorities)
        val token = JwtUtil.generateToken(UsernamePasswordAuthenticationToken(user, "", authorities))

        assertNotNull(token)
        println(token)
    }

    @Test
    fun `jwt 클레임과 함께 생성`() {
        val authorities = listOf(SimpleGrantedAuthority("GUEST"))
        val user = User("avante", "", authorities)
        val token = JwtUtil.generateToken(UsernamePasswordAuthenticationToken(user, "", authorities), mapOf("a" to "b", "c" to StringBuilder("xyz")))

        assertNotNull(token)
        println(token)
    }

    @Test
    fun `jwt 클레임 조회`() {
        val authorities = listOf(SimpleGrantedAuthority("GUEST"))
        val user = User("avante", "", authorities)
        val token = JwtUtil.generateToken(UsernamePasswordAuthenticationToken(user, "", authorities), mapOf("a" to "b", "c" to StringBuilder("xyz")))

        assertNotNull(token)
        println(token)

        val authentication = JwtUtil.getAuthentication(token.accessToken)
        val claims = JwtUtil.getAllClaims(token.accessToken)

        println(authentication)
        println(claims)
    }

    @Test
    fun `jwt validation success`() {
        val authorities = listOf(SimpleGrantedAuthority("GUEST"))
        val user = User("avante", "", authorities)
        val token = JwtUtil.generateToken(UsernamePasswordAuthenticationToken(user, "", authorities), mapOf("a" to "b", "c" to StringBuilder("xyz")))

        assertNotNull(token)

        assertTrue(JwtUtil.validateToken(token.accessToken))
    }

    @Test
    fun `jwt validation fail`() {
        val authorities = listOf(SimpleGrantedAuthority("GUEST"))
        val user = User("avante", "", authorities)
        val token = JwtUtil.generateToken(UsernamePasswordAuthenticationToken(user, "", authorities), mapOf("a" to "b", "c" to StringBuilder("xyz")))

        assertNotNull(token)

        assertThrows<CommonUnauthorizedException> { JwtUtil.validateToken("${token.accessToken}x") }
    }
}
