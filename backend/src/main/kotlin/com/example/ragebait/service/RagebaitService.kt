package com.example.ragebait.service

import com.example.ragebait.ai.GenerativeAiClient
import com.example.ragebait.entity.RagebaitPost
import com.example.ragebait.repository.RagebaitRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.TimeoutException

@Service
class RagebaitService(
    private val aiClient: GenerativeAiClient,
    private val repository: RagebaitRepository) {
    @Value("\${ai.client.timeout}")
    private lateinit var timeout: Duration

    fun generateTextAndSave(topic: String): Mono<RagebaitPost> {
        return aiClient.generateRagebait(topic)
            .timeout(timeout)
            .map { content ->
                RagebaitPost(topic = topic, content = content)
            }
            .flatMap { post ->
                Mono.fromCallable { repository.save(post) }
            }
            .onErrorResume(TimeoutException::class.java) {
                Mono.error(ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "Gateway Timeout"))
            }
    }

    fun getAll(): List<RagebaitPost> = repository.findAll()
}