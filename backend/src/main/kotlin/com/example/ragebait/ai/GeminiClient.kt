package com.example.ragebait.ai

import com.example.ragebait.model.gemini.GeminiResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.io.IOException
import java.time.Duration

@Component
class GeminiClient(private val webClient: WebClient) : GenerativeAiClient {

    companion object {
        private val logger = LoggerFactory.getLogger(GeminiClient::class.java)
    }

    @Value("\${gemini.prompt-template}")
    lateinit var promptTemplate: String

    override fun generateRagebait(topic: String): Mono<String> {
        val prompt = promptTemplate.format(topic)
        logger.debug("Generated Prompt: {}", truncate(prompt, 120))

        val requestBody = buildRequest(prompt)

        return webClient.post()
            .uri("/v1/models/gemini-2.0-flash-lite:generateContent")
            .bodyValue(requestBody)
            .retrieve()
            .onStatus({ it.isError }, { response ->
                response.bodyToMono(String::class.java).flatMap { body ->
                    logger.error("Gemini error {}: {}", response.statusCode(), body)
                    Mono.error(RuntimeException("Gemini error ${response.statusCode()}: $body"))
                }
            })
            .bodyToMono(GeminiResponse::class.java)
            .doOnSubscribe { logger.info("Requesting ragebait content for topic: {}", topic) }
            .retryWhen(
                Retry.backoff(3, Duration.ofSeconds(1))
                    .maxBackoff(Duration.ofSeconds(10))
                    .jitter(0.5)
                    .filter { shouldRetry(it) }
                    .doBeforeRetry { retrySignal ->
                        logger.warn("Retrying Gemini API due to error: {}", retrySignal.failure().message)
                    }
                    .onRetryExhaustedThrow { _, signal -> signal.failure() }
            )
            .timeout(Duration.ofSeconds(10))
            .map { response ->
                val text = response.candidates.firstOrNull()
                    ?.content?.parts?.firstOrNull()?.text ?: "[Empty Gemini response]"
                logger.info("Generated ragebait response: {}", truncate(text, 120))
                text
            }
    }

    private fun shouldRetry(throwable: Throwable): Boolean = when (throwable) {
        is WebClientResponseException -> throwable.statusCode.value() in listOf(429, 500, 502, 503, 504)
        is IOException -> true
        else -> false
    }

    private fun buildRequest(prompt: String): Map<String, Any> = mapOf(
        "contents" to listOf(
            mapOf("parts" to listOf(mapOf("text" to prompt)))
        )
    )

    private fun truncate(text: String?, limit: Int): String {
        return if (text == null || text.length <= limit) text ?: "" else text.substring(0, limit) + "..."
    }
}