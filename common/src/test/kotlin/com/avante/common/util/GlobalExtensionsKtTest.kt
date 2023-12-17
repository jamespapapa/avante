package com.avante.common.util

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GlobalExtensionsKtTest {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun emptyForNullTest() {
        val nullStr: String? = null
        val normalString = "string"

        assertEquals("", nullStr.emptyForNull())
        assertEquals("string", normalString.emptyForNull())
    }

    @Test
    fun defaultForNullTest() {
        val nullStr: String? = null
        val normalString = "string"

        assertEquals("Default String", nullStr.defaultForNull("Default String"))
        assertEquals("string", normalString.defaultForNull("Default String"))
    }

    @Test
    fun prefixOrEmptyForNullTest() {
        val nullStr: String? = null
        val prefix = "See ->"
        val normalString = "string"

        assertEquals("", nullStr.prefixOrEmptyForNull(prefix))
        assertEquals("See -> string", normalString.prefixOrEmptyForNull(prefix))
    }
}
