package com.example.ragebait.service

import com.example.ragebait.entity.AppUser
import com.example.ragebait.repository.AppUserRepository
import com.example.ragebait.security.AuthenticationFilter
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AppUserService(
    private val appUserRepository: AppUserRepository
) {
    private val logger = org.slf4j.LoggerFactory.getLogger(AppUserService::class.java)

    fun syncPremiumStatus(): Mono<AppUser> {
        return Mono.deferContextual { ctx ->
            val userId = ctx.getOrDefault(AuthenticationFilter.USER_ID_CONTEXT_KEY, null as String?)
                ?: return@deferContextual Mono.error(RuntimeException("User ID not found in context"))

            logger.info("Processing sync for userId: {}", userId)

            Mono.fromCallable {
                val user = appUserRepository.findById(userId).orElseGet {
                    logger.info("User not found, creating new user for: {}", userId)
                    AppUser(id = userId)
                }
                user.isPremium = true
                val savedUser = appUserRepository.save(user)
                logger.info("User {} saved with premium status", userId)
                savedUser
            }.subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
        }
    }
}
