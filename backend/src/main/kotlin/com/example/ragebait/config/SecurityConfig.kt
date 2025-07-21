package com.example.ragebait.config

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Bucket4j
import io.github.bucket4j.Refill
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.HandlerInterceptor
import java.time.Duration
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Configuration
class SecurityConfig : WebMvcConfigurer {

    @Bean
    fun rateLimitInterceptor(): RateLimitInterceptor {
        return RateLimitInterceptor()
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(rateLimitInterceptor())
            .addPathPatterns("/api/**")
    }
}

class RateLimitInterceptor : HandlerInterceptor {
    private val buckets = mutableMapOf<String, Bucket>()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val ip = request.remoteAddr
        val bucket = buckets.computeIfAbsent(ip) { createNewBucket() }

        if (bucket.tryConsume(1)) {
            return true
        }

        response.status = HttpStatus.TOO_MANY_REQUESTS.value()
        response.writer.write("Rate limit exceeded. Try again later.")
        return false
    }

    private fun createNewBucket(): Bucket {
        val requestLimit = 100L // requests
        val refillTokens = 100L
        val refillDuration = Duration.ofMinutes(1)
        val refill = Refill.intervally(refillTokens, refillDuration)
        val bandwidth = Bandwidth.classic(requestLimit, refill)
        return Bucket4j.builder().addLimit(bandwidth).build()
    }
} 