package com.my.homework.repository

import com.my.homework.vo.DisplayVO
import com.my.homework.vo.RelationVO

interface WordRepository<T> {
    val words: MutableMap<String, T>
    val relationTop: MutableList<RelationVO>
    val displayTop: MutableList<DisplayVO>
    fun clear()
    fun findById(key: String): T?
    fun save(key: String, value: T)
    fun exists(key: String): Boolean
    fun saveDisplayTop(list: List<DisplayVO>)
    fun saveRelationTop(list: List<RelationVO>)
    fun findDisplayTop10(): List<DisplayVO>
    fun findRelationTop10(): List<RelationVO>
}
