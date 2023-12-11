package com.my.homework.service.kakao

import com.my.homework.common.annotation.MyCacheEvict
import com.my.homework.common.annotation.MyCacheable
import com.my.homework.dto.Book
import com.my.homework.dto.kakao.Document
import com.my.homework.external.kakao.KakaoApiClient
import com.my.homework.repository.kakao.KakaoDocumentRepository
import com.my.homework.repository.kakao.KakaoWordRepository
import com.my.homework.service.BookService
import com.my.homework.vo.DisplayVO
import com.my.homework.vo.Group
import com.my.homework.vo.RelationVO
import com.my.homework.vo.kakao.VO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Service
import java.lang.Integer.min

@Service
class KakaoBookService(
    private val kakaoApiClient: KakaoApiClient,
    private val wordRepository: KakaoWordRepository,
    private val documentRepository: KakaoDocumentRepository
) : BookService {
    private val log = LoggerFactory.getLogger(this::class.java)

    @MyCacheEvict
    override suspend fun init(keyword: String?) {
        val start = System.currentTimeMillis()
        var finished = false
        var consumedCount = 0
        generateApiCallResponse(keyword ?: "코틀린", 20)
            .mapNotNull { it.await() }
            .forEach { bookResponse ->
                if (!finished) {
                    consumedCount += bookResponse.documents?.size ?: 0
                    documentRepository.saveAll(
                        bookResponse.documents?.filter {
                            !documentRepository.exists(it.isbn?:"")
                        } ?: listOf()
                    )
                }

                if (bookResponse.meta?.isEnd?.toBooleanStrictOrNull() == true) finished = true
            }

        if (documentRepository.count() < 1000) {
            /** Fill default value with keyword '자바' **/
            generateApiCallResponse("자바", 20)
                .mapNotNull { it.await() }
                .forEach { bookResponse ->
                    if (documentRepository.count() < 1000) {
                        val filtered = bookResponse.documents?.filter { !documentRepository.exists(it.isbn ?: "") } ?: listOf()
                        consumedCount += filtered.size
                        documentRepository.saveAll(filtered)
                    }
                }
        }
        buildWords(documentRepository.findAll())

        val fin = System.currentTimeMillis()

        log.info("init completed. total time spent : {}ms", fin - start)
    }

    private fun generateApiCallResponse(keyword: String, repeat: Int) =
        List(repeat) { it }
            .map { i ->
                CoroutineScope(Dispatchers.IO).async {
                    kakaoApiClient.getBooks(i + 1, 50, keyword)
                }
            }

    @Synchronized
    private fun buildWords(books: List<Document>) {
        var groupId = 0

        val wordsCounter = mutableMapOf<String, Int>()
        val relationCounter = mutableMapOf<String, Int>()
        wordRepository.clear()

        books.forEach { book ->
            val voList = book.words?.map { VO(it) }

            val groups = mutableListOf<Group>()
            val groupMap = mutableMapOf<String, Group>()
            voList?.forEach { vo ->
                val firstWord = vo.word.substring(0, 1)

                val g: Group
                if (!groupMap.containsKey(firstWord) || groupMap[firstWord]!!.groupCount == 10) {
                    // 새로운 그룹 (최초) 또는 그룹 꽉 참
                    val newGroup = Group(book.title ?: "unknown", firstWord, groupId++, 1)
                    groups.add(newGroup)
                    groupMap[firstWord] = newGroup

                    g = newGroup
                } else {
                    // 기존 그룹에 추가
                    g = groupMap[firstWord]!!
                    g.groupCount++
                }

                g.members.add(vo)

                if (!wordRepository.exists(vo.word)) {
                    // 새로운 word 추가
                    vo.group.add(g)
                    wordRepository.save(vo.word, vo)
                    wordsCounter[vo.word] = 1
                } else {
                    // 기존 word에 parent만 추가
                    wordRepository.findById(vo.word)!!.group.add(g)
                    wordsCounter[vo.word] = wordsCounter[vo.word]!! + 1
                }
            }

            // build Relation Top
            groups.forEach { g ->
                g.members.forEachIndexed { idx, vo ->
                    for (i in idx + 1 until g.members.size) {
                        val orderBy = listOf(vo, g.members[i]).sortedBy { it.word }
                        val key = "${orderBy[0].word}_${orderBy[1].word}"
                        if (relationCounter.containsKey(key)) {
                            relationCounter[key] = relationCounter[key]!! + 1
                        } else {
                            relationCounter[key] = 1
                        }
                    }
                }
            }
        }
        wordRepository.saveDisplayTop(
            generateTop10(wordsCounter) {
                val wordVo = wordRepository.findById(it.first)!!
                val relatedWords = wordVo.group
                    .flatMap { group -> group.members.map { vo -> vo.word } }
                    .distinct()
                DisplayVO(wordVo.word, relatedWords)
            }
        )
        wordRepository.saveRelationTop(
            generateTop10(relationCounter) {
                val split = it.first.split("_")
                val vo1 = wordRepository.findById(split[0])
                val vo2 = wordRepository.findById(split[1])
                val bookTitles =
                    vo1?.group?.filter { vo1Group -> vo2?.group?.any { vo2Group -> vo2Group.groupId == vo1Group.groupId } == true }
                        ?.map { group -> group.bookTitle }
                        ?: listOf()
                RelationVO(split[0], split[1], bookTitles)
            }
        )
    }

    private inline fun <T> generateTop10(map: Map<String, Int>, action: (Pair<String, Int>) -> T) =
        map.toList()
            .sortedByDescending { it.second }
            .subList(0, 10)
            .map { action(it) }

    @MyCacheable(name = "queryCache", keys = "q1,q2")
    override suspend fun query(q1: String, q2: String): List<String> {
        return if (wordRepository.findById(q1) == null || wordRepository.findById(q2) == null) {
            listOf()
        } else {
            wordRepository.findById(q1)!!.group.filter { q1Group ->
                wordRepository.findById(q2)!!.group.any { q2Group -> q2Group.groupId == q1Group.groupId }
            }.map { it.bookTitle }
        }
    }

    @MyCacheable(name = "displayTopCache")
    override suspend fun displayTop() = wordRepository.findDisplayTop10()

    @MyCacheable(name = "relationTopCache")
    override suspend fun relationTop() = wordRepository.findRelationTop10()

    @MyCacheEvict
    override suspend fun putWord(idx: Int?, word: String): String {
        resolveIdx(idx) { addWord(it, word) }
        return "Put Word OK ($idx , $word)"
    }

    @MyCacheEvict
    override suspend fun deleteWord(idx: Int?, word: String): String {
        resolveIdx(idx) { removeWord(it, word) }
        return "Delete Word OK ($idx , $word)"
    }

    private inline fun resolveIdx(idx: Int?, action: (doc: Document) -> Document) {
        when (idx) {
            null -> {
                val newList = documentRepository.findAll()
                    .map { action(it) }
                documentRepository.clear()
                documentRepository.saveAll(newList)
            }
            else -> {
                val doc = documentRepository.findById(idx)
                doc?.let { documentRepository.save(idx, action(it)) }
            }
        }
        buildWords(documentRepository.findAll())
    }

    override suspend fun documentIdx(title: String) = documentRepository.findIdByTitle(title)

    @MyCacheable(name = "findBooksByTitleCache", keys = "type,bookTitle,page,size")
    override suspend fun findBooksByTitle(type: String, bookTitle: String?, page: Int, size: Int): Slice<Book> {
        val pageable = PageRequest.of(page, size)
        val data = documentRepository.findAllByBookTitle(
            bookTitle,
            type,
            if (type == "slice") PageRequest.of(page, size + 1) else pageable
        )
        return wrapSliceApplyingType(type, data, pageable, documentRepository.countByBookTitle(bookTitle))
    }

    @MyCacheable(name = "findWordsByBookTitleCache", keys = "type,bookTitle,page,size")
    override suspend fun findWordsByBookTitle(type: String, bookTitle: String?, page: Int, size: Int): Slice<String> {
        val pageable = PageRequest.of(page, size)
        val data = documentRepository.findAllWordsByBookTitle(
            bookTitle,
            type,
            if (type == "slice") PageRequest.of(page, size + 1) else pageable
        )
        return wrapSliceApplyingType(type, data, pageable, documentRepository.countWordsByBookTitle(bookTitle))
    }

    private fun <T> wrapSliceApplyingType(type: String, data: List<T>, pageable: Pageable, count: Long? = null): Slice<T> =
        if (type == "normal") {
            PageImpl(data, pageable, count ?: 0)
        } else {
            SliceImpl(data.subList(0, min(data.size, pageable.pageSize)), pageable, pageable.pageSize < data.size)
        }

    private fun addWord(document: Document, word: String): Document {
        document.contents = "${document.contents} $word"

        return document
    }

    private fun removeWord(document: Document, word: String): Document {
        document.title = document.title?.replace(word, "")
        document.contents = document.contents?.replace(word, "")

        return document
    }
}
