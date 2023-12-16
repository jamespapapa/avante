package com.avante.common.util

import com.avante.common.dto.JwtToken
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import java.security.Key
import java.util.Date

object JwtUtil {
    private const val SECRET = "5725327ed7764b156efbf2eb3ba696e284e843b58efcc799b8b7bc55624e2a5e"
    private val key: Key
    private val log = LoggerFactory.getLogger(this::class.java)

    init {
        val keyBytes = Decoders.BASE64.decode(SECRET)
        this.key = Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateToken(name: String, roles: List<String>, additionalClaims: Map<String, Any>? = null): JwtToken {
        val authorities = roles.map { SimpleGrantedAuthority(it) }
        val user = User(name, "", authorities)
        return generateToken(UsernamePasswordAuthenticationToken(user, "", authorities), additionalClaims)
    }

    fun generateToken(authentication: Authentication, additionalClaims: Map<String, Any>? = null): JwtToken {
        val now = Date().time

        val authorities = authentication.authorities
            .joinToString(",") { it.authority }

        val claims = mutableMapOf<String, Any>("sub" to authentication.name, "rls" to authorities)
        additionalClaims?.let { claims.putAll(it) }

        val accessTokenExpiresIn = Date(now + 86400000)
        val accessToken = Jwts.builder()
            .setSubject(authentication.name)
            .setClaims(claims)
            .setExpiration(accessTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

        val refreshToken: String = Jwts.builder()
            .setExpiration(Date(now + 86400000))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

        return JwtToken("Bearer", accessToken, refreshToken)
    }

    fun getAuthentication(accessToken: String): Authentication {
        val claims = parseClaims(accessToken)
        val authorities = claims["rls"].toString()
            .split(",")
            .map { SimpleGrantedAuthority(it) }

        val principal = User(claims["sub"].toString(), "", authorities)
        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    fun getAllClaims(accessToken: String): Claims {
        return parseClaims(accessToken)
    }

    fun validateToken(token: String?): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            return true
        } catch (e: SecurityException) {
            log.warn("Invalid JWT Token", e)
        } catch (e: MalformedJwtException) {
            log.warn("Invalid JWT Token", e)
        } catch (e: ExpiredJwtException) {
            log.warn("Expired JWT Token", e)
        } catch (e: UnsupportedJwtException) {
            log.warn("Unsupported JWT Token", e)
        } catch (e: IllegalArgumentException) {
            log.warn("JWT claims string is empty.", e)
        } catch (e: SignatureException) {
            log.warn("JWT signature does not match locally computed signature.", e)
        }
        return false
    }

    private fun parseClaims(accessToken: String): Claims {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .body
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }
}
