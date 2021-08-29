import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.12.RELEASE"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.5.21"
	kotlin("plugin.spring") version "1.5.21"
}

group = "br.com.devcave.sqs"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

val springCloudVersion = "Hoxton.SR12"
val awsCloudVersion = "2.3.1"
val swaggerVersion = "3.0.0"
val braveInstrumentationAWSSQSVersion = "0.23.2"

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("org.springframework.cloud:spring-cloud-starter-aws")
	implementation("org.springframework.cloud:spring-cloud-aws-messaging")

	implementation("io.zipkin.aws:brave-instrumentation-aws-java-sdk-sqs:$braveInstrumentationAWSSQSVersion")
	implementation("org.springframework.cloud:spring-cloud-starter-sleuth")

	// Swagger
	implementation("io.springfox:springfox-boot-starter:$swaggerVersion")
	implementation("io.springfox:springfox-swagger-ui:$swaggerVersion")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
//		mavenBom("io.awspring.cloud:spring-cloud-aws-dependencies:$awsCloudVersion")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
