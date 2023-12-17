package com.avante.common.util

import com.avante.common.dto.CommonResponse
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType

object ServletUtil {
    fun writeResponse(res: HttpServletResponse, data: CommonResponse<String>) {
        res.status = data.status
        val json: String = jacksonObjectMapper().writeValueAsString(data)
        res.contentType = MediaType.APPLICATION_JSON_VALUE
        res.writer.write(json)
        res.writer.flush()
    }
}
