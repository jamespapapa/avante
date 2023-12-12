package com.avante.common.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.GrantedAuthority
import java.util.*

class JwtUtil {

    companion object {

        const val SECRET = "abcdeabcdeabcdeabcdeabcdeabcde12"
        const val EXP = 60 * 30
        fun buildJwt(sub: String, roles: List<GrantedAuthority>): String {
            val claims = mutableMapOf<String, Any>()

            claims["sub"] = sub
            claims["rls"] = roles

            return buildJwtWithClaims(claims)
        }

        private fun buildJwtWithClaims(claims: Map<String, Any>): String {
            val now = Date()
            val expire = Date(now.time + EXP * 1000)

            return Jwts.builder()
                .setSubject(claims["sub"] as String)
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .setIssuedAt(now)
                .setExpiration(expire)
                .setClaims(claims)
                .compact()
        }

        fun getClaims(jwt: String): Map<String, Any> {
            return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(jwt)
                .body
        }
    }
}
