package com.my.homework

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
class HomeworkApplication

fun main(args: Array<String>) {
    runApplication<HomeworkApplication>(*args)
}
