package com.example.ragebait.service

import com.example.ragebait.ai.GenerativeAiClient
import com.example.ragebait.entity.RagebaitPost
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration
import java.util.concurrent.TimeoutException
import org.springframework.web.server.ResponseStatusException

@SpringBootTest
class RagebaitServiceTest {

    @MockitoBean
    private lateinit var aiClient: GenerativeAiClient

    @Autowired
    private lateinit var ragebaitService: RagebaitService

    @Test
    fun `should return generated text`() {
        val topic = "cats"
        val fakeOutput = "Cats are overrated pets."

        whenever(aiClient.generateRagebait(topic)).thenReturn(Mono.just(fakeOutput))

        val result = ragebaitService.generateTextAndSave(topic)

        StepVerifier.create(result)
            .expectNextMatches { post ->
                post.topic == topic && post.content == fakeOutput
            }
            .verifyComplete()
    }

    @Test
    fun `should handle AI client failure`() {
        val topic = "dogs"
        whenever(aiClient.generateRagebait(topic))
            .thenReturn(Mono.error(RuntimeException("AI service unavailable")))

        val result = ragebaitService.generateTextAndSave(topic)

        StepVerifier.create(result)
            .expectError(RuntimeException::class.java)
            .verify()
    }

    @Test
    fun `should handle timeout from AI client`() {
        val topic = "birds"
        whenever(aiClient.generateRagebait(topic))
            .thenReturn(Mono.delay(Duration.ofSeconds(4))
                .thenReturn("Birds are not real"))

        val result = ragebaitService.generateTextAndSave(topic)

        StepVerifier.create(result)
            .expectError(ResponseStatusException::class.java)
            .verify(Duration.ofSeconds(4))
    }

    @Test
    fun `should handle empty topic`() {
        val topic = ""
        whenever(aiClient.generateRagebait(topic))
            .thenReturn(Mono.just("Empty topic response"))

        val result = ragebaitService.generateTextAndSave(topic)

        StepVerifier.create(result)
            .expectNextMatches { post ->
                post.topic == topic && post.content == "Empty topic response"
            }
            .verifyComplete()
    }
}