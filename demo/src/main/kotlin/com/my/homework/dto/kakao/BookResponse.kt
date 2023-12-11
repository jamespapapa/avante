package com.my.homework.dto.kakao

data class BookResponse(
    var documents: List<Document>? = null,
    var meta: Meta? = null
)
