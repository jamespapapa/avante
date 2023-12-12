package com.avante.memberapi.http.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/hello")
class HelloController {

    @GetMapping
    fun hello() = "hello"
}
