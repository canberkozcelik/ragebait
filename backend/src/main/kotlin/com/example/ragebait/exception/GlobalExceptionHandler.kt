package com.example.ragebait.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.bind.support.WebExchangeBindException
import java.time.LocalDateTime
import java.util.UUID

@RestControllerAdvice
class GlobalExceptionHandler {

    private fun getRequestId(exchange: ServerWebExchange): String =
        exchange.request.headers.getFirst("X-Request-Id") ?: UUID.randomUUID().toString()

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(ex: Exception, exchange: ServerWebExchange): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            message = ex.message ?: "An unexpected error occurred",
            path = exchange.request.path.toString(),
            requestId = getRequestId(exchange)
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException, exchange: ServerWebExchange): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = ex.statusCode.value(),
            error = ex.reason ?: "Error",
            message = ex.message ?: "An error occurred",
            path = exchange.request.path.toString(),
            requestId = getRequestId(exchange)
        )
        return ResponseEntity.status(ex.statusCode).body(error)
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleWebExchangeBindException(ex: WebExchangeBindException, exchange: ServerWebExchange): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
        val error = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation failure",
            message = errors.joinToString(", "),
            path = exchange.request.path.toString(),
            requestId = getRequestId(exchange)
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException, exchange: ServerWebExchange): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
        val error = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation failure",
            message = errors.joinToString(", "),
            path = exchange.request.path.toString(),
            requestId = getRequestId(exchange)
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(RateLimitExceededException::class)
    fun handleRateLimitExceededException(ex: RateLimitExceededException, exchange: ServerWebExchange): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.TOO_MANY_REQUESTS.value(),
            error = "Rate limit exceeded",
            message = ex.message ?: "Rate limit exceeded",
            path = exchange.request.path.toString(),
            requestId = getRequestId(exchange)
        )
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error)
    }
}

data class ErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val requestId: String
)