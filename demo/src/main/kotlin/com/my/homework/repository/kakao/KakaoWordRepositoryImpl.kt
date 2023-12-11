package com.my.homework.repository.kakao

import com.my.homework.common.annotation.MyLogging
import com.my.homework.vo.DisplayVO
import com.my.homework.vo.RelationVO
import com.my.homework.vo.kakao.VO
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class KakaoWordRepositoryImpl(
    override val words: MutableMap<String, VO> = mutableMapOf(),
    override val relationTop: MutableList<RelationVO> = mutableListOf(),
    override val displayTop: MutableList<DisplayVO> = mutableListOf()
) : KakaoWordRepository {
    private val log = LoggerFactory.getLogger(this::class.java)

    @MyLogging override fun clear() {
        words.clear()
        relationTop.clear()
        displayTop.clear()
    }
    override fun findById(key: String) = words[key]
    override fun save(key: String, value: VO) { words[key] = value }
    override fun exists(key: String) = words.containsKey(key)

    @MyLogging override fun saveDisplayTop(list: List<DisplayVO>) { displayTop.addAll(list) }

    @MyLogging override fun saveRelationTop(list: List<RelationVO>) { relationTop.addAll(list) }

    @MyLogging override fun findDisplayTop10(): List<DisplayVO> = displayTop.toList()

    @MyLogging override fun findRelationTop10(): List<RelationVO> = relationTop.toList()
}
