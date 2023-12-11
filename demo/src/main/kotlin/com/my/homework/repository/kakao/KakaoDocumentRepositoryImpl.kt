package com.my.homework.repository.kakao

import com.my.homework.common.annotation.MyLogging
import com.my.homework.dto.kakao.Document
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.lang.Integer.min

@Repository
class KakaoDocumentRepositoryImpl(
    override val documents: MutableList<Document>,
    val size: Int = 1000
) : KakaoDocumentRepository {
    private val log = LoggerFactory.getLogger(this::class.java)

    @MyLogging override fun clear() = documents.clear()

    @MyLogging override fun findAll(): List<Document> = documents.subList(0, size)
    override fun saveAll(list: List<Document>): Int {
        var saveCount = list.size

        if (documents.size + list.size > size) {
            saveCount = size - documents.size
            log.warn("Size is full. Only $saveCount document(s) will be saved.")
        }
        documents.addAll(
            list.map { book ->
                book.words = extractWords(book)
                book
            }.subList(0, saveCount)
        )
        return saveCount
    }

    @MyLogging override fun findIdByTitle(title: String) = documents.indexOfFirst { it.title == title }
    override fun count(): Int = documents.size
    override fun exists(isbn: String) = documents.any { it.isbn == isbn }

    @MyLogging override fun findById(id: Int): Document? {
        if (id > documents.size) {
            log.error("$id is requested even if documents have only ${documents.size} records.")
            return null
        }
        return documents[id]
    }

    @MyLogging override fun findAllByBookTitle(bookTitle: String?, type: String, pageable: Pageable): List<Document> {
        val filtered = documents.filter { doc -> bookTitle?.let { doc.title?.lowercase()?.contains(it.lowercase()) } ?: true }
        val offsetTypeApplied = buildOffsetApplyingType(type, pageable)
        if (offsetTypeApplied > filtered.size) {
            log.error("${pageable.offset} requested. event if only having ${filtered.size} records.")
            return emptyList()
        }
        return filtered.subList(
            min(offsetTypeApplied, filtered.size),
            min(offsetTypeApplied + pageable.pageSize, filtered.size)
        )
    }

    @MyLogging override fun findAllWordsByBookTitle(bookTitle: String?, type: String, pageable: Pageable): List<String> {
        val words = findAllByBookTitle(bookTitle, type, PageRequest.of(0, 1000))
            .flatMap { book -> book.words ?: listOf() }
            .distinct()
            .sorted()

        if (words.isEmpty()) return words
        val offsetApplied = buildOffsetApplyingType(type, pageable)
        return words.subList(min(offsetApplied, words.size), min(offsetApplied + pageable.pageSize, words.size))
    }

    private fun buildOffsetApplyingType(type: String, pageable: Pageable) =
        (if (type == "slice") pageable.pageNumber * (pageable.pageSize - 1).toLong() else pageable.offset).toInt()

    @MyLogging override fun countByBookTitle(bookTitle: String?) =
        documents.filter { doc -> bookTitle?.let { doc.title?.lowercase()?.contains(it.lowercase()) } ?: true }
            .size.toLong()

    @MyLogging override fun countWordsByBookTitle(bookTitle: String?) =
        documents.filter { doc -> bookTitle?.let { doc.title?.lowercase()?.contains(it.lowercase()) } ?: true }
            .flatMap { book -> book.words ?: listOf() }
            .distinct()
            .size.toLong()

    @MyLogging override fun save(index: Int, document: Document) {
        if (index > documents.size) {
            log.error("$index is overflow. current document size is ${documents.size}.")
            log.error("Nothing to do.")
        } else {
            documents[index] = document
            document.words = extractWords(document)
        }
    }

    private fun extractWords(document: Document) = extractTarget(document)
        .replace("[^ㄱ-ㅎ가-힣a-zA-Z0-9\\s]".toRegex(), "")
        .lowercase()
        .split(" ", "\n")
        .distinct()
        .filter { it.isNotBlank() }
        .toMutableList()

    private fun extractTarget(document: Document) = "${document.title ?: ""} ${document.contents ?: ""}"
}
