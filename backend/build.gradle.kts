plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.5"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.jetbrains.kotlin.plugin.jpa") version "1.9.25"
	id("jacoco")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.flywaydb:flyway-core")
	implementation("org.springframework.boot:spring-boot-starter-logging")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	implementation("com.google.oauth-client:google-oauth-client:1.33.3")
	implementation("com.google.apis:google-api-services-oauth2:v2-rev157-1.25.0")
	implementation("com.google.api-client:google-api-client:1.35.0")
	implementation("com.google.auth:google-auth-library-oauth2-http:1.11.0")

	// OpenAPI Documentation
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.4.0")

	// Rate Limiting
	implementation("com.github.vladimir-bukhtoyarov:bucket4j-core:7.6.0")

	// Monitoring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-registry-prometheus")

	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
	testImplementation("io.projectreactor:reactor-test")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.jacocoTestReport {
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
	classDirectories.setFrom(
		fileTree("${project.buildDir}/classes/kotlin/main") {
			exclude(
				"**/config/**",
				"**/model/**"
			)
		}
	)
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			limit {
				minimum = "0.65".toBigDecimal()
			}
		}
	}
	classDirectories.setFrom(
		fileTree("${project.buildDir}/classes/kotlin/main") {
			exclude(
				"**/config/**",
				"**/model/**"
			)
		}
	)
}

tasks.test {
	finalizedBy(tasks.jacocoTestReport)
	finalizedBy(tasks.jacocoTestCoverageVerification)
}
