package com.my.homework.dto.kakao

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Meta(
    var isEnd: String? = null,
    var pageableCount: Int? = null,
    var totalCount: Int? = null
)
