package com.avante.config.security

import com.avante.common.dto.CommonResponse
import com.avante.common.exception.CommonException
import com.avante.common.exception.CommonUnauthorizedException
import com.avante.common.util.JwtUtil
import com.avante.common.util.ServletUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean

class JwtAuthenticationFilter(
    var whiteList: List<String> = listOf()
) : GenericFilterBean() {
    private val log = LoggerFactory.getLogger(this::class.java)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        try {
            if (secured(request as HttpServletRequest)) {
                val token = request.getHeader("Authorization")
                val realToken = resolveBearerToken(token, response as HttpServletResponse)

                if (JwtUtil.validateToken(realToken)) {
                    val authentication: Authentication = JwtUtil.getAuthentication(realToken)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }

            chain.doFilter(request, response)
        } catch (e: CommonException) {
            val errorResponse = CommonResponse(e.status.value(), e.status.reasonPhrase, e.localizedMessage)
            ServletUtil.writeResponse(response as HttpServletResponse, errorResponse)
        }
    }

    private fun secured(request: HttpServletRequest): Boolean {
        return !whiteList.contains(request.requestURI)
    }

    private fun resolveBearerToken(token: String?, res: HttpServletResponse): String {
        if (token != null && token.startsWith("Bearer")) {
            return token.substring(7)
        }
        log.warn(token?.let { "Token should start with 'Bearer '" } ?: "Authorization token is not present.")

        throw CommonUnauthorizedException(token?.let { "Token should start with 'Bearer '" } ?: "Authorization token is not present.")
    }
}
