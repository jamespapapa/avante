package com.my.homework.dto.kakao

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.my.homework.dto.Book

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Document(
    override var title: String? = null,
    var contents: String? = null,
    var authors: List<String>? = null,
    var isbn: String? = null,
    var url: String? = null,
    var status: String? = null,
    var salePrice: String? = null,
    var publisher: String? = null,
    var datetime: String? = null,
    var words: MutableList<String>? = mutableListOf()
) : Book(title)
