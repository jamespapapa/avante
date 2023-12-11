package com.my.homework.repository

import org.springframework.data.domain.Pageable

interface DocumentRepository<T> {
    val documents: MutableList<T>
    fun clear()
    fun findAll(): List<T>
    fun saveAll(list: List<T>): Int
    fun findIdByTitle(title: String): Int
    fun count(): Int
    fun exists(isbn: String): Boolean
    fun findById(id: Int): T?
    fun findAllByBookTitle(bookTitle: String?, type: String, pageable: Pageable): List<T>
    fun findAllWordsByBookTitle(bookTitle: String?, type: String, pageable: Pageable): List<String>
    fun countByBookTitle(bookTitle: String?): Long
    fun countWordsByBookTitle(bookTitle: String?): Long
    fun save(index: Int, document: T)
}
