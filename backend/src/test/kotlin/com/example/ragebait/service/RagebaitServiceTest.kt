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

@SpringBootTest(properties = ["ai.client.timeout=2s"])
class RagebaitServiceTest {

    @MockitoBean
    private lateinit var aiClient: GenerativeAiClient

    @MockitoBean
    private lateinit var appUserRepository: com.example.ragebait.repository.AppUserRepository

    @MockitoBean
    private lateinit var requestLogRepository: com.example.ragebait.repository.RequestLogRepository

    @Autowired
    private lateinit var ragebaitService: RagebaitService

    @Test
    fun `should return generated text for premium user`() {
        val topic = "cats"
        val fakeOutput = "Cats are overrated pets."
        val userId = "test-user"

        whenever(aiClient.generateRagebait(topic)).thenReturn(Mono.just(fakeOutput))
        whenever(appUserRepository.findById(userId)).thenReturn(java.util.Optional.of(com.example.ragebait.entity.AppUser(userId, true)))

        val result = ragebaitService.generateTextAndSave(topic)
            .contextWrite(reactor.util.context.Context.of(com.example.ragebait.security.AuthenticationFilter.USER_ID_CONTEXT_KEY, userId))

        StepVerifier.create(result)
            .expectNextMatches { post ->
                post.topic == topic && post.content == fakeOutput
            }
            .verifyComplete()
    }

    @Test
    fun `should handle AI client failure`() {
        val topic = "dogs"
        val userId = "test-user"
        
        whenever(appUserRepository.findById(userId)).thenReturn(java.util.Optional.of(com.example.ragebait.entity.AppUser(userId, true)))
        whenever(aiClient.generateRagebait(topic))
            .thenReturn(Mono.error(RuntimeException("AI service unavailable")))

        val result = ragebaitService.generateTextAndSave(topic)
            .contextWrite(reactor.util.context.Context.of(com.example.ragebait.security.AuthenticationFilter.USER_ID_CONTEXT_KEY, userId))

        StepVerifier.create(result)
            .expectError(RuntimeException::class.java)
            .verify()
    }

    @Test
    fun `should handle timeout from AI client`() {
        val topic = "birds"
        val userId = "test-user"

        whenever(appUserRepository.findById(userId)).thenReturn(java.util.Optional.of(com.example.ragebait.entity.AppUser(userId, true)))
        whenever(aiClient.generateRagebait(topic))
            .thenReturn(Mono.delay(Duration.ofSeconds(4))
                .thenReturn("Birds are not real"))

        val result = ragebaitService.generateTextAndSave(topic)
            .contextWrite(reactor.util.context.Context.of(com.example.ragebait.security.AuthenticationFilter.USER_ID_CONTEXT_KEY, userId))

        StepVerifier.create(result)
            .expectError(ResponseStatusException::class.java)
            .verify(Duration.ofSeconds(4))
    }

    @Test
    fun `should handle empty topic`() {
        val topic = ""
        val userId = "test-user"

        whenever(appUserRepository.findById(userId)).thenReturn(java.util.Optional.of(com.example.ragebait.entity.AppUser(userId, true)))
        whenever(aiClient.generateRagebait(topic))
            .thenReturn(Mono.just("Empty topic response"))

        val result = ragebaitService.generateTextAndSave(topic)
             .contextWrite(reactor.util.context.Context.of(com.example.ragebait.security.AuthenticationFilter.USER_ID_CONTEXT_KEY, userId))

        StepVerifier.create(result)
            .expectNextMatches { post ->
                post.topic == topic && post.content == "Empty topic response"
            }
            .verifyComplete()
    }
}