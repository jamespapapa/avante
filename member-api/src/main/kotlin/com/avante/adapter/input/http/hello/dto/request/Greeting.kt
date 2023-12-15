package com.avante.adapter.input.http.hello.dto.request

import jakarta.validation.constraints.NotNull

data class Greeting(
    @field:NotNull val message: String?
)
