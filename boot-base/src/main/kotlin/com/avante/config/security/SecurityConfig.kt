package com.avante.config.security

import com.avante.common.dto.CommonResponse
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import java.io.PrintWriter

@Configuration
@EnableWebSecurity
@ConfigurationProperties(prefix = "auth")
class SecurityConfig(
    var whiteList: List<String> = listOf()
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .cors {
                val config = CorsConfiguration()
                config.setAllowedOriginPatterns(listOf("*"))
                config.allowedMethods = listOf("*")
                config.allowCredentials = true
                config.allowedHeaders = listOf("*")
                config.maxAge = 600L

                config
            }
            .headers { it.frameOptions { f -> f.disable() }.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(PathRequest.toH2Console()).permitAll()
                    .requestMatchers("/auth/login").permitAll()
                    .requestMatchers("/hello").hasRole("USER")
                    .anyRequest().authenticated()
            }
            .addFilterBefore(JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling { configurer ->
                configurer
                    .accessDeniedHandler { _, res, _ ->
                        val forbidden = CommonResponse(HttpStatus.FORBIDDEN.value(), "Forbidden", "Check your authorization status.")
                        writeResponse(res, forbidden)
                    }
                    .authenticationEntryPoint { _, res, _ ->
                        val unauthorized = CommonResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", "You are unauthorized.")
                        writeResponse(res, unauthorized)
                    }
            }

        return http.build()
    }

    private fun writeResponse(res: HttpServletResponse, data: CommonResponse<String>) {
        res.status = data.status
        val json: String = jacksonObjectMapper().writeValueAsString(data)
        res.contentType = MediaType.APPLICATION_JSON_VALUE
        val writer: PrintWriter = res.writer
        writer.write(json)
        writer.flush()
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
