package com.example.ragebait.controller

import com.example.ragebait.entity.AppUser
import com.example.ragebait.security.AuthenticationFilter
import com.example.ragebait.service.AppUserService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf


@WebFluxTest(
    controllers = [AppUserController::class],
    excludeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [AuthenticationFilter::class])]
)
class AppUserControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var appUserService: AppUserService

    @Test
    @WithMockUser
    fun `syncPremiumStatus should return success`() {
        val mockUser = AppUser(id = "test-user", isPremium = true)
        `when`(appUserService.syncPremiumStatus()).thenReturn(Mono.just(mockUser))

        webTestClient
            .mutateWith(csrf())
            .post().uri("/api/v1/user/sync")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.success").isEqualTo(true)
            .jsonPath("$.isPremium").isEqualTo(true)
    }
}
