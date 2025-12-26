package com.example.ragebait.config

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Bucket4j
import io.github.bucket4j.Refill
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.time.Duration

@Component
class RateLimitFilter : WebFilter {

    private val buckets = mutableMapOf<String, Bucket>()

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val path = exchange.request.path.value()

        // Only rate limit API endpoints
        if (!path.startsWith("/api")) {
            return chain.filter(exchange)
        }

        val ip = exchange.request.remoteAddress?.address?.hostAddress ?: "unknown"
        val bucket = buckets.computeIfAbsent(ip) { createNewBucket() }

        if (bucket.tryConsume(1)) {
            return chain.filter(exchange)
        }

        exchange.response.statusCode = HttpStatus.TOO_MANY_REQUESTS
        exchange.response.headers.contentType = MediaType.TEXT_PLAIN
        val bytes = "Rate limit exceeded. Try again later.".toByteArray(StandardCharsets.UTF_8)
        val buffer = exchange.response.bufferFactory().wrap(bytes)
        return exchange.response.writeWith(Mono.just(buffer))
    }

    private fun createNewBucket(): Bucket {
        val requestLimit = 100L // requests
        val refillTokens = 100L
        val refillDuration = Duration.ofMinutes(1)
        val refill = Refill.intervally(refillTokens, refillDuration)
        val bandwidth = Bandwidth.classic(requestLimit, refill)
        return Bucket4j.builder().addLimit(bandwidth).build()
    }
} 