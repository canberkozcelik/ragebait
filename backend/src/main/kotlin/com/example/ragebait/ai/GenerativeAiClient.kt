package com.example.ragebait.ai

import reactor.core.publisher.Mono

interface GenerativeAiClient {
    fun generateRagebait(topic: String): Mono<String>
}