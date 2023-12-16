package com.avante.config.security

import com.avante.common.util.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean

class JwtAuthenticationFilter: GenericFilterBean() {
    private val log = LoggerFactory.getLogger(this::class.java)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val token = (request as HttpServletRequest).getHeader("Authorization")

        resolveBearerToken(token)?.let{
            if (JwtUtil.validateToken(it)) {
                val authentication: Authentication = JwtUtil.getAuthentication(it)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        chain.doFilter(request, response)
    }

    private fun resolveBearerToken(token: String?): String? {
        if (token != null && token.startsWith("Bearer")) {
            return token.substring(7)
        }
        log.warn("Invalid JWT Token -> {}", token)
        return null
    }
}
