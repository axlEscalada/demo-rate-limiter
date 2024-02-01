import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-redis") {
		exclude(group = "io.lettuce.lettuce-core")
	}
	implementation("com.github.houbb:redis-client-jedis:0.0.3")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("javax.validation:validation-api:2.0.1.Final")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(module = "mockito-core")
	}
	testImplementation("io.mockk:mockk:1.13.7")

	// IntegrationTest
	testImplementation("com.redis.testcontainers:testcontainers-redis-junit:1.6.4")

}

tasks.withType<Test> {
	useJUnitPlatform()

	testLogging {
		events("passed", "skipped", "failed")
	}
}

val integrationTest: SourceSet = sourceSets.create("integrationTest") {
	java {
		compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
		runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
		srcDir("src/integrationTest/kotlin")
	}
	resources.srcDir("src/integrationTest/resources")
}

configurations[integrationTest.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())
configurations[integrationTest.runtimeOnlyConfigurationName].extendsFrom(configurations.testRuntimeOnly.get())

val integrationTestTask = tasks.register<Test>("integrationTest") {
	group = "verification"

	useJUnitPlatform()

	testClassesDirs = integrationTest.output.classesDirs
	classpath = sourceSets["integrationTest"].runtimeClasspath

	shouldRunAfter("test")
}

tasks.check {
	dependsOn(integrationTestTask)
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
