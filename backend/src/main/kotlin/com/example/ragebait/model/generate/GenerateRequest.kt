package com.example.ragebait.model.generate

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class GenerateRequest(
    @field:NotBlank(message = "Topic must not be blank")
    @field:Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Topic must be alphanumeric")
    val topic: String
)