package com.my.homework.common.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.coroutines.Continuation

@Component
@Aspect
class LoggingAspect {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    fun restControllers() { }

    @Pointcut("@within(org.springframework.stereotype.Repository)")
    fun repositories() { }

    @Pointcut("@annotation(com.my.homework.common.annotation.MyLogging)")
    fun myLogging() { }

    @Around("restControllers() && myLogging()")
    fun restControllerAround(pjp: ProceedingJoinPoint): Any? {
        return loggingAround(pjp, "@RestController")
    }

    @Around("repositories() && myLogging()")
    fun repositoryAround(pjp: ProceedingJoinPoint): Any? {
        return loggingAround(pjp, "@Repository")
    }

    private fun loggingAround(pjp: ProceedingJoinPoint, type: String): Any? {
        log.info("<< $type begin : {}", pjp.args.filter { it !is Continuation<*> })
        val start = System.currentTimeMillis()
        val result = pjp.proceed()
        val fin = System.currentTimeMillis()

        log.info(
            "<< $type finish : {}({}) = {} ({}ms)",
            pjp.signature.declaringTypeName,
            pjp.signature.name,
            if (result?.toString()?.length ?: 0 > 2000) result.toString().substring(0, 2000) + "..." else result?.toString(),
            fin - start
        )
        return result
    }
}
