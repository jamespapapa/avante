package com.avante.adapter.input.http

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class ExceptionHandlerEntryPoint {
    @ExceptionHandler(value = [MismatchedInputException::class])
    fun missingFormatArgumentExceptionHandler(e: MismatchedInputException): ResponseEntity<Any> {
        val error = ErrorFrame(
            errors = listOf(
                ErrorContent(
                    type = "INVALID_PARAMETER",
                    message = e.message?: "Mismatched input params",
                    fieldName = "Unknown",
                    rejectedValue = null
                )
            )
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    // enum, LocalDateTime 매핑 오류.
    @ExceptionHandler(value = [InvalidFormatException::class])
    fun invalidFormatException(e: InvalidFormatException): ResponseEntity<Any> {
        val error = ErrorFrame(
            errors = listOf(
                ErrorContent(
                    type = "INVALID_PARAMETER",
                    message = "${e.value} is not acceptable",
                    fieldName = e.path.first().fieldName,
                    rejectedValue = e.value.toString()
                )
            )
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    // javax.validation.constraints 오류
    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<Any> {
        val error = ErrorFrame(
            errors = e.fieldErrors.map {
                ErrorContent(
                    type = "INVALID_PARAMETER",
                    message = it.defaultMessage ?: "유효하지 않은 값입니다.",
                    fieldName = it.field,
                    rejectedValue = it.rejectedValue?.toString()
                )
            }
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [MissingServletRequestParameterException::class])
    fun missingServletRequestParameterException(e: MissingServletRequestParameterException): ResponseEntity<Any> {
        val error = ErrorFrame(
            errors = listOf(
                ErrorContent(
                    type = "INVALID_PARAMETER",
                    message = "${e.parameterName} is missing or null",
                    fieldName = e.parameterName,
                    rejectedValue = null
                )
            )
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    // GetMapping 에서 LocalDateTime, Enum 오류발생시 사용
    @ExceptionHandler(value = [MethodArgumentTypeMismatchException::class])
    fun methodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<Any> {
        val error = ErrorFrame(
            errors = listOf(
                ErrorContent(
                    type = "INVALID_PARAMETER",
                    message = "${e.value} is not acceptable",
                    fieldName = e.name,
                    rejectedValue = e.value.toString()
                )
            )
        )
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }
    // Webflux 기준 PostMapping 오류
    @ExceptionHandler(value = [WebExchangeBindException::class])
    suspend fun webExchangeBindExceptionHandler(e: WebExchangeBindException): ResponseEntity<Any> {

        val errors = ErrorFrame(
            errors = e.fieldErrors.map {
                ErrorContent(
                    type = "INVALID_PARAMETER",
                    message = it.defaultMessage ?: e.localizedMessage,
                    fieldName = it.field,
                    rejectedValue = it.rejectedValue.toString()
                )
            })
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }
}

data class ErrorFrame(
    val errors: List<ErrorContent>
)

data class ErrorContent(
    val type: String,
    val message: String,
    val fieldName: String?,
    val rejectedValue: String?,
)
