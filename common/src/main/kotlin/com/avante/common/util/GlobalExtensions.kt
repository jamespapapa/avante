package com.avante.common.util

fun String?.emptyForNull(): String {
    return this ?: ""
}
fun String?.defaultForNull(defaultValue: String): String {
    return this ?: defaultValue
}

fun String?.prefixOrEmptyForNull(prefix: String): String {
    return this?.let { "$prefix $this" } ?: ""
}
