package com.example.ragebait.controller

import com.example.ragebait.entity.RagebaitPost
import com.example.ragebait.exception.RateLimitExceededException
import com.example.ragebait.service.RagebaitService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration
import java.util.concurrent.TimeoutException

@WebFluxTest(
    controllers = [RagebaitController::class],
    excludeFilters = [org.springframework.context.annotation.ComponentScan.Filter(
        type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
        classes = [com.example.ragebait.security.AuthenticationFilter::class]
    )]
)
@Import(RagebaitService::class)
class RagebaitControllerTest(@Autowired val webTestClient: WebTestClient) {

    @MockitoBean
    private lateinit var ragebaitService: RagebaitService

    @Test
    fun `should return generated ragebait text`() {
        val topic = "pineapple on pizza"
        val responseText = "Anyone who eats pineapple on pizza is simply criminal."

        whenever(ragebaitService.generateTextAndSave(topic)).thenReturn(
            Mono.just(RagebaitPost(topic = topic, content = responseText))
        )

        webTestClient.post()
            .uri("/api/v1/generate").contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"topic": "$topic"}""")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.result").isEqualTo(responseText)
    }

    @Test
    fun `should return 400 when the topic is blank`() {
        webTestClient.post()
            .uri("/api/v1/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"topic": ""}""")
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.error").isEqualTo("Validation failure")
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.path").isEqualTo("/api/v1/generate")
            .jsonPath("$.requestId").isNotEmpty()
    }

    @Test
    fun `should retry on failure and return success`() {
        val topic = "test topic"
        val responseText = "Generated text after retries."

        whenever(ragebaitService.generateTextAndSave(topic))
            .thenReturn(
                Mono.error<RagebaitPost>(RuntimeException("Temporary failure"))
                    .retryWhen(Retry.backoff(2, Duration.ofSeconds(1)))
                    .onErrorReturn(RagebaitPost(topic = topic, content = responseText))
            )

        webTestClient.post()
            .uri("/api/v1/generate").contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"topic": "$topic"}""")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.result").isEqualTo(responseText)
    }

    @Test
    fun `should return 504 Gateway Timeout when request times out`() {
        val topic = "slow api"

        whenever(ragebaitService.generateTextAndSave(topic))
            .thenReturn(Mono.error(TimeoutException()))

        webTestClient.post()
            .uri("/api/v1/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"topic": "$topic"}""")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.GATEWAY_TIMEOUT)
            .expectBody()
            .jsonPath("$.status").isEqualTo(504)
            .jsonPath("$.error").isEqualTo("Gateway Timeout")
            .jsonPath("$.path").isEqualTo("/api/v1/generate")
    }

    @Test
    fun `should return 400 when request body is missing`() {
        webTestClient.post()
            .uri("/api/v1/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.error").isEqualTo("No request body")
            .jsonPath("$.status").isEqualTo(400)
    }

    @Test
    fun `should return 415 when content type is not JSON`() {
        webTestClient.post()
            .uri("/api/v1/generate")
            .contentType(MediaType.TEXT_PLAIN)
            .bodyValue("plain text")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    }

    @Test
    fun `should return 400 when JSON is malformed`() {
        webTestClient.post()
            .uri("/api/v1/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"topic": "test" invalid json""")
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.error").isEqualTo("Failed to read HTTP message")
    }

    @Test
    fun `should return 429 when rate limit is exceeded`() {
        val topic = "rate limit test"
        whenever(ragebaitService.generateTextAndSave(topic))
            .thenReturn(Mono.error(RateLimitExceededException()))

        webTestClient.post()
            .uri("/api/v1/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"topic": "$topic"}""")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
            .expectBody()
            .jsonPath("$.error").isEqualTo("Rate limit exceeded")
            .jsonPath("$.status").isEqualTo(429)
    }
}