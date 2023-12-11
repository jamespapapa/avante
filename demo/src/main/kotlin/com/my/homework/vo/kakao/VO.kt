package com.my.homework.vo.kakao

import com.my.homework.vo.Group
import com.my.homework.vo.WordVo

data class VO(override val word: String, var group: MutableList<Group> = mutableListOf()) : WordVo(word)
