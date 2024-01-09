package com.avante.config.security

import com.avante.common.dto.CommonResponse
import com.avante.common.util.ServletUtil
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.core.GrantedAuthorityDefaults
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
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
            }
            .headers { it.frameOptions { f -> f.disable() }.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(PathRequest.toH2Console()).permitAll()
                    .requestMatchers("/members/v1/sign-up").permitAll()
                    .requestMatchers("/members/v1/login").permitAll()
                    .requestMatchers("/hello").hasRole("USER")
                    .anyRequest().authenticated()
            }
            .addFilterBefore(JwtAuthenticationFilter(whiteList), UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling { configurer ->
                configurer
                    .accessDeniedHandler { _, res, _ -> // TODO "_" 이게 뭘 의미하는 건지 모르겠어요...(함수형 인터페이스 파악)
                        val forbidden = CommonResponse(HttpStatus.FORBIDDEN.value(), "Forbidden", "Check your authorization status.")
                        ServletUtil.writeResponse(res, forbidden)
                    }
                    .authenticationEntryPoint { _, res, _ ->
                        val unauthorized = CommonResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", "You are unauthorized.")
                        ServletUtil.writeResponse(res, unauthorized)
                    }
            }

        return http.build()
    }

    @Bean
    fun grantedAuthorityDefaults(): GrantedAuthorityDefaults {
        return GrantedAuthorityDefaults("") // Remove the ROLE_ prefix
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
