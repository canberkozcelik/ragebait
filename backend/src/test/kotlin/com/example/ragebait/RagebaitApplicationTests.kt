package com.example.ragebait

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class RagebaitApplicationTests {

	@Autowired
	private lateinit var applicationContext: ApplicationContext

	@Test
	fun `context loads successfully`() {
		// This test will fail if the application context fails to load
		assert(applicationContext.getBeanDefinitionNames().isNotEmpty()) { "Application context should have beans" }
	}

	@Test
	fun `main application bean is present`() {
		val mainBean = applicationContext.getBean(RagebaitApplication::class.java)
		assert(mainBean is RagebaitApplication) { "Main application bean should be present" }
	}

}
