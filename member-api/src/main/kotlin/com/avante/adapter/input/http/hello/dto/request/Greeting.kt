package com.avante.adapter.input.http.hello.dto.request

import jakarta.validation.constraints.NotNull

data class Greeting(
    @field:NotNull val message: String? // TODO 생성자에 특별히 쓰이는 유효성 검사 어노테이션인지 파악
)
