package com.example.ragebait.service

import com.example.ragebait.entity.AppUser
import com.example.ragebait.repository.AppUserRepository
import com.example.ragebait.security.AuthenticationFilter
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.Optional

class AppUserServiceTest {

    private val appUserRepository = mock(AppUserRepository::class.java)
    private val appUserService = AppUserService(appUserRepository)

    @Test
    fun `syncPremiumStatus should update user to premium`() {
        val userId = "test-user-id"
        val existingUser = AppUser(id = userId, isPremium = false)

        `when`(appUserRepository.findById(userId)).thenReturn(Optional.of(existingUser))
        `when`(appUserRepository.save(any(AppUser::class.java))).thenAnswer { it.arguments[0] }

        StepVerifier.create(
            appUserService.syncPremiumStatus()
                .contextWrite { it.put(AuthenticationFilter.USER_ID_CONTEXT_KEY, userId) }
        )
            .assertNext { user ->
                assertTrue(user.isPremium)
            }
            .verifyComplete()

        verify(appUserRepository).save(argThat { it.isPremium })
    }

    @Test
    fun `syncPremiumStatus should create new user if not exists`() {
        val userId = "new-user-id"

        `when`(appUserRepository.findById(userId)).thenReturn(Optional.empty())
        `when`(appUserRepository.save(any(AppUser::class.java))).thenAnswer { it.arguments[0] }

        StepVerifier.create(
            appUserService.syncPremiumStatus()
                .contextWrite { it.put(AuthenticationFilter.USER_ID_CONTEXT_KEY, userId) }
        )
            .assertNext { user ->
                assertTrue(user.isPremium)
                assertTrue(user.id == userId)
            }
            .verifyComplete()
    }
}
