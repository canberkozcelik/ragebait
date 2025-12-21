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
    private val repository: RagebaitRepository,
    private val appUserRepository: com.example.ragebait.repository.AppUserRepository,
    private val requestLogRepository: com.example.ragebait.repository.RequestLogRepository
) {
    @Value("\${ai.client.timeout}")
    private lateinit var timeout: Duration

    fun generateTextAndSave(topic: String): Mono<RagebaitPost> {
        return Mono.deferContextual { ctx ->
            val userId = ctx.getOrDefault(com.example.ragebait.security.AuthenticationFilter.USER_ID_CONTEXT_KEY, null as String?)
                ?: return@deferContextual Mono.error(com.example.ragebait.security.UnauthorizedException("User ID not found in context"))

            Mono.fromCallable {
                // Ensure user exists
                val user = appUserRepository.findById(userId).orElseGet {
                    appUserRepository.save(com.example.ragebait.entity.AppUser(id = userId))
                }
                
                // Check quota
                if (!user.isPremium) {
                    val count = requestLogRepository.countByUserId(userId)
                    if (count >= 3) {
                        throw com.example.ragebait.exception.QuotaExceededException("You have used your 3 free ragebaits. Upgrade to generate unlimited rage!")
                    }
                }
                userId
            }.subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
            .flatMap { verifiedUserId ->
                aiClient.generateRagebait(topic)
                    .timeout(timeout)
                    .flatMap { content ->
                        Mono.fromCallable {
                            // Save the post
                            val savedPost = repository.save(RagebaitPost(topic = topic, content = content))
                            // Log the request
                            requestLogRepository.save(com.example.ragebait.entity.RequestLog(userId = verifiedUserId, topic = topic))
                            savedPost
                        }.subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                    }
            }
            .onErrorResume(TimeoutException::class.java) {
                Mono.error(ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "Gateway Timeout"))
            }
        }
    }

    fun getAll(): List<RagebaitPost> = repository.findAll()
}