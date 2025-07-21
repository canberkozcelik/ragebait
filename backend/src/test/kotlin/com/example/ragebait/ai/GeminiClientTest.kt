package com.example.ragebait.ai

import com.example.ragebait.model.gemini.GeminiResponse
import com.example.ragebait.model.gemini.Candidate
import com.example.ragebait.model.gemini.Content
import com.example.ragebait.model.gemini.Part
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.any
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import org.mockito.kotlin.any as kAny

class GeminiClientTest {

    @Test
    fun `should return text on successful response`() {
        val webClient = mock(WebClient::class.java)
        val client = GeminiClient(webClient)
        client.promptTemplate = "Ragebait about %s"
        val topic = "birds"
        val expectedText = "Birds are not real"
        val response = GeminiResponse(
            candidates = listOf(
                Candidate(
                    content = Content(
                        parts = listOf(
                            Part(text = expectedText)
                        )
                    )
                )
            )
        )

        val requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec::class.java)
        val requestBodySpec = mock(WebClient.RequestBodySpec::class.java)
        val responseSpec = mock(WebClient.ResponseSpec::class.java)

        `when`(webClient.post()).thenReturn(requestBodyUriSpec)
        `when`(requestBodyUriSpec.uri("/v1/models/gemini-2.0-flash-lite:generateContent")).thenReturn(requestBodySpec)
        `when`(requestBodySpec.bodyValue(any())).thenReturn(requestBodySpec)
        `when`(requestBodySpec.retrieve()).thenReturn(responseSpec)
        `when`(responseSpec.bodyToMono(GeminiResponse::class.java)).thenReturn(Mono.just(response))
        `when`(responseSpec.onStatus(kAny(), kAny())).thenReturn(responseSpec)

        val result = client.generateRagebait(topic)

        StepVerifier.create(result)
            .expectNext(expectedText)
            .verifyComplete()
    }
} 