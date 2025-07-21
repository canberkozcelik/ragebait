plugins {
    id("org.springframework.boot") version "3.4.5" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    kotlin("jvm") version "1.9.25" apply false
    kotlin("plugin.spring") version "1.9.25" apply false
}

allprojects {
    group = "com.ragebait"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "21"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
} 