package com.example.ragebait.repository

import com.example.ragebait.entity.AppUser
import com.example.ragebait.entity.RequestLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AppUserRepository : JpaRepository<AppUser, String>

@Repository
interface RequestLogRepository : JpaRepository<RequestLog, Long> {
    
    @Query("SELECT COUNT(r) FROM RequestLog r WHERE r.userId = :userId")
    fun countByUserId(userId: String): Long
}
