package com.example.ragebait.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import java.io.IOException

@Configuration
class FirebaseConfig {

    private val logger = LoggerFactory.getLogger(FirebaseConfig::class.java)

    @Value("\${gemini.credentials.json}")
    lateinit var credentialsResource: Resource

    @Bean
    fun firebaseApp(): FirebaseApp {
        try {
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(credentialsResource.inputStream))
                .build()

            if (FirebaseApp.getApps().isEmpty()) {
                logger.info("Initializing Firebase App...")
                return FirebaseApp.initializeApp(options)
            }
            return FirebaseApp.getInstance()
        } catch (e: IOException) {
            logger.error("Failed to initialize Firebase: {}", e.message)
            throw RuntimeException("Failed to initialize Firebase", e)
        }
    }
}
