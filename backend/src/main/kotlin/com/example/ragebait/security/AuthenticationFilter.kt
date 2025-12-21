package com.example.ragebait.security

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.util.context.Context

@Component
class AuthenticationFilter : WebFilter {

    private val logger = LoggerFactory.getLogger(AuthenticationFilter::class.java)

    companion object {
        const val USER_ID_CONTEXT_KEY = "USER_ID"
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val path = exchange.request.path.value()
        
        // Public endpoints - Skip auth
        if (path.startsWith("/api-docs") || 
            path.startsWith("/swagger-ui") || 
            path.startsWith("/h2-console") ||
            path.startsWith("/actuator")) {
            return chain.filter(exchange)
        }

        val authHeader = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // For now, allow unauthenticated usage? 
            // The plan says we need auth to enforce quota. 
            // If strictly anonymous auth is required, we should block.
            // Let's Reject.
            return Mono.error(UnauthorizedException("Missing or invalid Authorization header"))
        }

        val token = authHeader.substring(7)
        return try {
            // FirebaseAuth is blocking, so we ideally wrap in blocking pool if heavy, 
            // but verifying token is usually fast enough or we accept the minor block for simplicity.
            // For high performace, verifyIdTokenAsync should be converted to Mono.
            
            // Using a simple blocking call for MVP
            val decodedToken = FirebaseAuth.getInstance().verifyIdToken(token)
            val uid = decodedToken.uid
            
            chain.filter(exchange)
                .contextWrite(Context.of(USER_ID_CONTEXT_KEY, uid))
                
        } catch (e: Exception) {
            logger.warn("Authentication failed: {}", e.message)
            Mono.error(UnauthorizedException("Invalid token"))
        }
    }
}

class UnauthorizedException(message: String) : RuntimeException(message)
