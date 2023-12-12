plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
}

tasks.bootJar { enabled = false }

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // jwt
    implementation("io.jsonwebtoken:jjwt:0.9.1")

    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")

    // Google collections
    implementation("com.google.guava:guava:23.0")

    // H2 DB
    runtimeOnly("com.h2database:h2")

    // mockk
    implementation("io.mockk:mockk:1.10.2")
}
