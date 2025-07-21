package com.example.ragebait.repository

import com.example.ragebait.entity.RagebaitPost
import org.springframework.data.jpa.repository.JpaRepository

interface RagebaitRepository : JpaRepository<RagebaitPost, Long> {
    fun findByTopicContainingIgnoreCase(topic: String): List<RagebaitPost>
}