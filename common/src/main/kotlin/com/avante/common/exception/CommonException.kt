package com.avante.common.exception

import com.avante.common.util.prefixOrEmptyForNull
import org.springframework.http.HttpStatus

open class CommonException(
    val status: HttpStatus,
    override val message: String? // TODO 생성자의 파라미터를 오버라이드 한다는게 무슨 의미인지 파악
) : RuntimeException(message ?: status.reasonPhrase)

class CommonNotFoundException(reason: String? = null) : CommonException(HttpStatus.NOT_FOUND, "Not Found. ${reason?.prefixOrEmptyForNull("See ->")}")
class CommonUnauthorizedException(reason: String? = null) : CommonException(HttpStatus.UNAUTHORIZED, "Unauthorized. ${reason?.prefixOrEmptyForNull("See ->")}")
class CommonForbiddenException(reason: String? = null) : CommonException(HttpStatus.FORBIDDEN, "Forbidden. ${reason?.prefixOrEmptyForNull("See ->")}")
class CommonBadRequestException(reason: String? = null) : CommonException(HttpStatus.BAD_REQUEST, "Bad Request. ${reason?.prefixOrEmptyForNull("Reason ->")}")
