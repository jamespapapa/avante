package com.my.homework.service

import com.my.homework.dto.Book
import com.my.homework.vo.DisplayVO
import com.my.homework.vo.RelationVO
import org.springframework.data.domain.Slice

interface BookService {
    suspend fun init(keyword: String? = "코틀린")
    suspend fun query(q1: String, q2: String): List<String>
    suspend fun displayTop(): List<DisplayVO>
    suspend fun relationTop(): List<RelationVO>
    suspend fun putWord(idx: Int?, word: String): String
    suspend fun deleteWord(idx: Int?, word: String): String
    suspend fun documentIdx(title: String): Int
    suspend fun findBooksByTitle(type: String = "normal", bookTitle: String?, page: Int, size: Int): Slice<Book>
    suspend fun findWordsByBookTitle(type: String = "normal", bookTitle: String?, page: Int, size: Int): Slice<String>
}
