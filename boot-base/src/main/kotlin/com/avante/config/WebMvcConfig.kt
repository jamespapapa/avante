package com.avante.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
@EnableCaching
@ConfigurationProperties(prefix = "auth.cors")
class WebMvcConfig(
    var allowedOrigin: List<String> = listOf()
) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedHeaders("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
            .allowedOrigins(*allowedOrigin.toTypedArray())
    }
}
