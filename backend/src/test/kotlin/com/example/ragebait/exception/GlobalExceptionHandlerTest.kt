package com.example.ragebait.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange

class GlobalExceptionHandlerTest {
    private val handler = GlobalExceptionHandler()
    private val exchange = mock(ServerWebExchange::class.java)
    private val request = mock(ServerHttpRequest::class.java)
    private val headers = mock(HttpHeaders::class.java)

    init {
        whenever(exchange.request).thenReturn(request)
        whenever(request.path).thenReturn(MockServerHttpRequest.get("/test").build().path)
        whenever(request.headers).thenReturn(headers)
        whenever(headers.getFirst(any())).thenReturn(null)
    }

    @Test
    fun `handleGlobalException returns 500`() {
        val ex = Exception("Something went wrong")
        val response = handler.handleGlobalException(ex, exchange)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("Internal Server Error", response.body?.error)
        assertEquals("Something went wrong", response.body?.message)
    }

    @Test
    fun `handleResponseStatusException returns correct status and message`() {
        val ex = ResponseStatusException(HttpStatus.NOT_FOUND, "Not found")
        val response = handler.handleResponseStatusException(ex, exchange)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("Not found", response.body?.error)
        // message will include the exception class name, so just check it contains the reason
        assert(response.body?.message?.contains("Not found") == true)
    }

    @Test
    fun `handleValidationException returns 400`() {
        val bindingResult = mock(org.springframework.validation.BindingResult::class.java)
        val fieldError = org.springframework.validation.FieldError("object", "field", "must not be blank")
        org.mockito.Mockito.`when`(bindingResult.fieldErrors).thenReturn(listOf(fieldError))
        val ex = MethodArgumentNotValidException(mock(org.springframework.core.MethodParameter::class.java), bindingResult)
        val response = handler.handleValidationException(ex, exchange)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Validation failure", response.body?.error)
        assert(response.body?.message?.contains("field: must not be blank") == true)
    }

    @Test
    fun `handleRateLimitExceededException returns 429`() {
        val ex = RateLimitExceededException()
        val response = handler.handleRateLimitExceededException(ex, exchange)
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.statusCode)
        assertEquals("Rate limit exceeded", response.body?.error)
    }
} 