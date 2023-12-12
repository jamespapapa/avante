

plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
}

dependencies {
    implementation(project(":common"))
    implementation(project(":boot-base"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // jwt
    implementation("io.jsonwebtoken:jjwt:0.9.1")

    // springdoc
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")

    // mapstruct
    implementation("org.mapstruct:mapstruct:1.4.1.Final")
    kapt("org.mapstruct:mapstruct-processor:1.4.1.Final")

    // queryDsl
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt::jpa")

    // Google collections
    implementation("com.google.guava:guava:23.0")

    // H2 DB
    runtimeOnly("com.h2database:h2")

    // mockk
    implementation("io.mockk:mockk:1.10.2")
}
