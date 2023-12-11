package com.my.homework.controller

import com.my.homework.dto.InitRequest
import com.my.homework.dto.WordRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.util.LinkedMultiValueMap

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "20000")
class MyControllerTest {

    @Autowired
    private lateinit var wtc: WebTestClient

    @BeforeEach
    fun set() { }

    @Test
    fun `초기화가 성공한다`() {
        val request = InitRequest("코틀린")
        wtc.post()
            .uri("/init")
            .bodyValue(request)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }
    }

    @Test
    fun `초기화가 후 쿼리에 정상 응답한다`() {
        val request = InitRequest("코틀린")
        wtc.post()
            .uri("/init")
            .bodyValue(request)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }

        val params = LinkedMultiValueMap<String, String>()
        params["q1"] = "자바"
        params["q2"] = "자바를"

        val uri = "/query"
        wtc.get().uri { uriBuilder ->
            uriBuilder.path(uri)
                .queryParams(params)
                .build()
        }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }
    }

    @Test
    fun `초기화가 후 display Top 호출에 정상 응답한다`() {
        val request = InitRequest("코틀린")
        wtc.post()
            .uri("/init")
            .bodyValue(request)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }

        val uri = "/display-top"
        wtc.get().uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }
    }

    @Test
    fun `초기화가 후 relation Top 호출에 정상 응답한다`() {
        val request = InitRequest("코틀린")
        wtc.post()
            .uri("/init")
            .bodyValue(request)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }

        val uri = "/relation-top"
        wtc.get().uri(uri)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }
    }

    @Test
    fun `초기화가 후 쿼리에 단어를 삭제한 뒤에 결과와 추가 후 쿼리 결과가 다르다`() {
        val request = InitRequest("코틀린")
        wtc.post()
            .uri("/init")
            .bodyValue(request)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }

        val wordReq = WordRequest(null, "자12345")

        wtc.put().uri("/words")
            .bodyValue(wordReq)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk

        val params = LinkedMultiValueMap<String, String>()
        params["q1"] = "자바"
        params["q2"] = "자12345"

        val uri = "/query"
        wtc.get().uri { uriBuilder ->
            uriBuilder.path(uri)
                .queryParams(params)
                .build()
        }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }

        wtc.delete()
            .uri("/words?word=자12345")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk

        wtc.get().uri { uriBuilder ->
            uriBuilder.path(uri)
                .queryParams(params)
                .build()
        }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }
    }

    @Test
    fun `특수문자 제거 정규식 테스트`() {
        val input = "개발에 익숙한 사람보다는 막연한 개발자가 되고 싶거나, 개발에 \$어쩌고+^ 저쩌고는@"
        val expected = "개발에 익숙한 사람보다는 막연한 개발자가 되고 싶거나 개발에 어쩌고 저쩌고는"

        val regex = "[^ㄱ-ㅎ가-힣a-zA-Z0-9\\s]".toRegex()
        val result = input.replace(regex, "")
        assertEquals(expected, result)
    }

    @Test
    fun `초기화가 후 책 페이징 일반 호출에 정상 응답한다`() {
        val request = InitRequest("코틀린")
        wtc.post()
            .uri("/init")
            .bodyValue(request)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }

        val params = LinkedMultiValueMap<String, String>()
        params["title"] = "자바"
        params["page"] = "16"
        params["size"] = "50"
        params["type"] = "normal"

        val uri = "/books"
        wtc.get().uri { uriBuilder ->
            uriBuilder.path(uri)
                .queryParams(params)
                .build()
        }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }
    }

    @Test
    fun `초기화가 후 책 페이징 슬라이스 호출에 정상 응답한다`() {
        val request = InitRequest("코틀린")
        wtc.post()
            .uri("/init")
            .bodyValue(request)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }

        val params = LinkedMultiValueMap<String, String>()
        params["title"] = "자바"
        params["page"] = "16"
        params["size"] = "50"
        params["type"] = "slice"

        val uri = "/books"
        wtc.get().uri { uriBuilder ->
            uriBuilder.path(uri)
                .queryParams(params)
                .build()
        }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }
    }

    @Test
    fun `초기화가 후 단어 페이징 일반 호출에 정상 응답한다`() {
        val request = InitRequest("코틀린")
        wtc.post()
            .uri("/init")
            .bodyValue(request)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }

        val params = LinkedMultiValueMap<String, String>()
        params["title"] = "자바"
        params["page"] = "1"
        params["size"] = "40"
        params["type"] = "normal"

        val uri = "/book-words"
        wtc.get().uri { uriBuilder ->
            uriBuilder.path(uri)
                .queryParams(params)
                .build()
        }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }
    }

    @Test
    fun `초기화가 후 단어 페이징 슬라이스 호출에 정상 응답한다`() {
        val request = InitRequest("코틀린")
        wtc.post()
            .uri("/init")
            .bodyValue(request)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }

        val params = LinkedMultiValueMap<String, String>()
        params["title"] = "자바"
        params["page"] = "195"
        params["size"] = "40"
        params["type"] = "slice"

        val uri = "/book-words"
        wtc.get().uri { uriBuilder ->
            uriBuilder.path(uri)
                .queryParams(params)
                .build()
        }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }
    }

    @Test
    fun `초기화가 후 단어 페이징 슬라이스 호출이 캐싱된다`() {
        val request = InitRequest("코틀린")
        wtc.post()
            .uri("/init")
            .bodyValue(request)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }

        val params = LinkedMultiValueMap<String, String>()
        params["title"] = "자바"
        params["page"] = "195"
        params["size"] = "40"
        params["type"] = "slice"

        val uri = "/book-words"
        wtc.get().uri { uriBuilder ->
            uriBuilder.path(uri)
                .queryParams(params)
                .build()
        }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }

        wtc.get().uri { uriBuilder ->
            uriBuilder.path(uri)
                .queryParams(params)
                .build()
        }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }

        wtc.get().uri { uriBuilder ->
            uriBuilder.path(uri)
                .queryParams(params)
                .build()
        }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }

        wtc.get().uri { uriBuilder ->
            uriBuilder.path(uri)
                .queryParams(params)
                .build()
        }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }

        wtc.get().uri { uriBuilder ->
            uriBuilder.path(uri)
                .queryParams(params)
                .build()
        }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }

        wtc.get().uri { uriBuilder ->
            uriBuilder.path(uri)
                .queryParams(params)
                .build()
        }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }

        wtc.get().uri { uriBuilder ->
            uriBuilder.path(uri)
                .queryParams(params)
                .build()
        }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { println(it) }
    }
}
