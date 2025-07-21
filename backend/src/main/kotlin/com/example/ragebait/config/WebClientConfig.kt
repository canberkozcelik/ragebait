package com.example.ragebait.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class WebClientConfig(private val resourceLoader: ResourceLoader) {

    @Value("\${gemini.credentials.json}")
    lateinit var serviceAccountKeyPath: String

    @Value("\${gemini.base-url}")
    lateinit var geminiBaseUrl: String

    @Bean
    fun geminiAiWebClient(): WebClient {
        val credentials = loadGoogleCredentials()
        val authHeader = authHeader(credentials)

        return WebClient.builder()
            .baseUrl(geminiBaseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .filter(authHeader)
            .build()
    }

    private fun loadGoogleCredentials(): GoogleCredentials {
        val resource: Resource = resourceLoader.getResource(serviceAccountKeyPath)
        val credentialsStream = resource.inputStream
        val credentials = ServiceAccountCredentials.fromStream(credentialsStream)
            .createScoped(
                listOf(
                    "https://www.googleapis.com/auth/cloud-platform",
                    "https://www.googleapis.com/auth/generative-language"
                )
            )

        // Refresh the access token explicitly
        credentials.refreshIfExpired()

        // Ensure the token is valid before proceeding
        if (credentials.accessToken == null) {
            throw IllegalStateException("Failed to retrieve access token.")
        }

        return credentials
    }

    private fun authHeader(credentials: GoogleCredentials): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor { request ->
            Mono.just(request)
                .flatMap {
                    // Refresh the access token
                    credentials.refreshAccessToken()
                    val token = credentials.accessToken.tokenValue

                    // Add the Authorization header with the token
                    val updatedRequest = ClientRequest.from(request)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                        .build()

                    Mono.just(updatedRequest)
                }
        }
    }
}