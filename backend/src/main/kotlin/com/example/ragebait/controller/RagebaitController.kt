package com.example.ragebait.controller

import com.example.ragebait.model.generate.GenerateRequest
import com.example.ragebait.model.generate.GenerateResponse
import com.example.ragebait.service.RagebaitService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import java.util.concurrent.TimeoutException

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Ragebait", description = "Ragebait generation and management APIs")
class RagebaitController(private val ragebaitService: RagebaitService) {

    private val logger = LoggerFactory.getLogger(RagebaitController::class.java)

    @Operation(
        summary = "Generate ragebait content",
        description = "Generates ragebait content based on the provided topic"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully generated content",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = GenerateResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request parameters"
            ),
            ApiResponse(
                responseCode = "504",
                description = "Gateway Timeout"
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        ]
    )
    @PostMapping(
        "/generate",
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun generate(
        @Parameter(description = "Generation request parameters", required = true)
        @Valid @RequestBody request: GenerateRequest
    ): Mono<GenerateResponse> {
        logger.info("Received generate request with topic: {}", request.topic)
        return ragebaitService.generateTextAndSave(request.topic)
            .map { GenerateResponse(it.id, it.content) }
            .onErrorResume(TimeoutException::class.java) {
                Mono.error(ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "Gateway Timeout"))
            }
    }
}