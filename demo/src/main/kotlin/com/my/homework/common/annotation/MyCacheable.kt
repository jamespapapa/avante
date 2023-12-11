package com.my.homework.common.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class MyCacheable(
    val name: String,
    val keys: String = ""
)
