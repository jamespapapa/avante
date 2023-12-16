package com.avante.common.dto

data class JwtToken(
    val grantType: String,
    val accessToken: String,
    val refreshToken: String
)
