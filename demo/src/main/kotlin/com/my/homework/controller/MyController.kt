package com.my.homework.controller

import com.my.homework.common.annotation.MyLogging
import com.my.homework.dto.InitRequest
import com.my.homework.dto.WordRequest
import com.my.homework.service.BookService
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class MyController(
    private val bookService: BookService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/init")
    @MyLogging
    fun init(@RequestBody request: InitRequest): Mono<String> =
        mono {
            log.info("{} Initialization success.", "POST /init")
            bookService.init(request.keyword)
            "Init OK"
        }

    @GetMapping("/query")
    @MyLogging
    fun query(q1: String, q2: String) =
        mono {
            log.info("{} query {}(lowercased) {}(lowercased).", "GET /query", q1.lowercase(), q2.lowercase())
            bookService.query(q1.lowercase(), q2.lowercase())
        }

    @GetMapping("/display-top")
    @MyLogging
    fun displayTop() =
        mono {
            log.info("{} Display Top.", "GET /display-top")
            bookService.displayTop()
        }

    @GetMapping("/relation-top")
    @MyLogging
    fun relationTop() =
        mono {
            log.info("{} Relation Top.", "GET /relation-top")
            bookService.relationTop()
        }

    @PutMapping("/words")
    @MyLogging
    fun putWord(@RequestBody request: WordRequest) =
        mono {
            log.info("{} put word.", "PUT /words")
            bookService.putWord(request.idx, request.word)
        }

    @DeleteMapping("/words")
    @MyLogging
    fun deleteWord(idx: Int?, word: String) =
        mono {
            log.info("{} delete word.", "DELETE /words")
            bookService.deleteWord(idx, word)
        }

    @GetMapping("/document-idx")
    @MyLogging
    fun documentIdx(title: String) =
        mono {
            log.info("{} document idx.", "GET /document-idx")
            bookService.documentIdx(title)
        }

    @GetMapping("/books")
    @MyLogging
    fun getBooks(type: String = "normal", title: String?, page: Int = 0, size: Int = 10) =
        mono {
            log.info("{} get books.", "GET /books")
            bookService.findBooksByTitle(type, title, page, size)
        }

    @GetMapping("/book-words")
    @MyLogging
    fun getBookWords(type: String = "normal", title: String?, page: Int = 0, size: Int = 10) =
        mono {
            log.info("{} get book words.", "GET /book-words")
            bookService.findWordsByBookTitle(type, title, page, size)
        }
}
