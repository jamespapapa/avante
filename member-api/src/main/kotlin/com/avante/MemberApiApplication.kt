package com.avante

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class MemberApiApplication

ㅈfun main(args: Array<String>) {
    runApplication<MemberApiApplication>(*args)
}
