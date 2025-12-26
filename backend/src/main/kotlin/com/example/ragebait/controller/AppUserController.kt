package com.example.ragebait.controller

import com.example.ragebait.service.AppUserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/user")
class AppUserController(
    private val appUserService: AppUserService
) {

    private val logger = org.slf4j.LoggerFactory.getLogger(AppUserController::class.java)

    @PostMapping("/sync")
    fun syncPremiumStatus(): Mono<Map<String, Any>> {
        logger.info("Received request to sync premium status")
        return appUserService.syncPremiumStatus()
            .map { user ->
                logger.info("Successfully synced premium status for user: {}", user.id)
                mapOf<String, Any>("success" to true, "isPremium" to user.isPremium)
            }
            .doOnError { e ->
                logger.error("Error syncing premium status", e)
            }
    }
}
