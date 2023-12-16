package com.avante.adapter.input.http.hello

import com.avante.adapter.input.http.hello.dto.request.Greeting
import com.avante.adapter.input.http.hello.dto.response.Reaction
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/hello")
class HelloController {

    @PostMapping
    fun hello(
        @Valid @RequestBody
        dto: Greeting
    ): Reaction {
        return Reaction()
    }
}
