package com.example.ragebait.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "app_user")
data class AppUser(
    @Id
    val id: String, // Firebase UID

    @Column(name = "is_premium", nullable = false)
    var isPremium: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "request_log")
data class RequestLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "topic")
    val topic: String?,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
