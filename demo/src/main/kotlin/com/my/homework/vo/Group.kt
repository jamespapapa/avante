package com.my.homework.vo

import com.fasterxml.jackson.annotation.JsonIgnore

data class Group(
    val bookTitle: String,
    val word: String,
    val groupId: Int,
    var groupCount: Int,
    @JsonIgnore var members: MutableList<WordVo> = mutableListOf()
) {
    override fun toString(): String {
        return "Group(groupId=$groupId)"
    }
}
