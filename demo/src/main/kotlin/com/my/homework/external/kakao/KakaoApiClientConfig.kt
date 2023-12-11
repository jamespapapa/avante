package com.my.homework.external.kakao

import com.my.homework.external.WebClientBuildable
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class KakaoApiClientConfig(
    @Value("\${kakao.auth.token}") val apiToken: String?
) : WebClientBuildable {

    private val baseUrl: String = "https://dapi.kakao.com"

    @Bean
    @Qualifier("kakaoApiWebClient")
    fun kakaoApiWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .kakaoApiRequestHeaders()
            .commonClientConnector()
            .build()
    }

    fun WebClient.Builder.kakaoApiRequestHeaders() =
        this.defaultHeaders { headers ->
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            headers.add(HttpHeaders.AUTHORIZATION, "KakaoAK $apiToken")
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        }
}
