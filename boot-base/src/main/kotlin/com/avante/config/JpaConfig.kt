package com.avante.config

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.Optional

@Configuration
@EnableJpaAuditing
class JpaConfig : AuditorAware<String> {
    @PersistenceContext
    private val entityManager: EntityManager? = null
    override fun getCurrentAuditor(): Optional<String> =
        Optional.ofNullable("test")

    @Bean
    fun jpaQueryFactory(): JPAQueryFactory =
        JPAQueryFactory(entityManager)
}
