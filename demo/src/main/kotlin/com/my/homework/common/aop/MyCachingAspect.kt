package com.my.homework.common.aop

import com.my.homework.common.annotation.MyCacheable
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.Continuation

/**
 * This aspect is used only for suspend functions.
 * except suspend functions, recommended to use @Cacheable + @CacheEvict
 */
@Component
@Aspect
class MyCachingAspect {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Pointcut("@annotation(com.my.homework.common.annotation.MyCacheable)")
    fun cacheable() { }

    @Pointcut("@annotation(com.my.homework.common.annotation.MyCacheEvict)")
    fun cacheEvict() { }

    private val cacheManager = ConcurrentHashMap<String, Any>()

    @Around("cacheable()")
    fun cachingAround(pjp: ProceedingJoinPoint): Any? {
        log.info("<< Cache begin : {} {}", pjp.signature.name, pjp.args.filter { it !is Continuation<*> })
        val start = System.currentTimeMillis()

        val parameterNames = (pjp.signature as MethodSignature).parameterNames
        val cacheable = (pjp.signature as MethodSignature).method.getAnnotation(MyCacheable::class.java)
        val keys = cacheable.keys.split(",").filter { it.isNotBlank() }

        val cacheKey = "#${cacheable.name}_(${ keys.map { keyStr -> pjp.args[parameterNames.indexOfFirst { it == keyStr }]}.joinToString(
            ","
        ) })"

        if (cacheManager.containsKey(cacheKey)) {
            val fin = System.currentTimeMillis()
            log.info("Cache Hit ($cacheKey) : ({}ms)", fin - start)
            return cacheManager[cacheKey]
        }

        val result = pjp.proceed()
        cacheManager[cacheKey] = result
        val fin = System.currentTimeMillis()
        log.info("Invocation finished with caching ({}) : ({}ms)", cacheKey, fin - start)
        return result
    }

    @AfterReturning("cacheEvict()")
    fun cacheEvict(jp: JoinPoint) {
        log.info("(After Returning ${jp.signature.name}) Cache Evict Required : {} {} ", jp.signature.name, jp.args)
        cacheManager.clear()
        log.info("All cache evicted.")
    }
}
