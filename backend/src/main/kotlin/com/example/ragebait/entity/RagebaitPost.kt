package com.example.ragebait.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
data class RagebaitPost(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val topic: String,
    @Column(length = 280)
    val content: String,
    val createdAt: Instant = Instant.now()
)