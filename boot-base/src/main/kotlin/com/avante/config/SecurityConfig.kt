package com.avante.config

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration

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
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(PathRequest.toH2Console()).permitAll()
                    .anyRequest().permitAll()
            }.exceptionHandling {
                it.accessDeniedHandler { _, res, _ -> res.sendRedirect("/denied") }
                    .authenticationEntryPoint { _, res, _ -> res.sendRedirect("/denied") }
            }.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

        return http.build()
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
