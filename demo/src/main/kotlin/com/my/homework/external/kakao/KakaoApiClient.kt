package com.my.homework.external.kakao

import com.my.homework.dto.kakao.BookResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull

@Component
class KakaoApiClient(
    @Qualifier("kakaoApiWebClient") private val client: WebClient
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    suspend fun getBooks(page: Int, size: Int, keyword: String?) =
        client.get()
            .uri("/v3/search/book?target=title${nullCheck(keyword)}&page=$page&size=$size")
            .retrieve()
            .awaitBodyOrNull<BookResponse>()

    private fun nullCheck(keyword: String?) =
        keyword?.let { "&query=$it" } ?: ""
}
