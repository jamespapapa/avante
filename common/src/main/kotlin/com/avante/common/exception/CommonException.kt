package com.avante.common.exception

import org.springframework.http.HttpStatus

open class CommonException(
    private val status: HttpStatus,
    override val message: String?
) : RuntimeException(message ?: status.reasonPhrase)

class UserNotFoundException : CommonException(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다.")
class JwtNotPresentException : CommonException(HttpStatus.UNAUTHORIZED, "JWT토큰이 없습니다.")
class JwtExpiredException : CommonException(HttpStatus.UNAUTHORIZED, "JWT토큰이 만료 되었습니다.")
class JwtNotValidException : CommonException(HttpStatus.UNAUTHORIZED, "JWT토큰이 유효하지 않습니다.")
