import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

plugins {
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"

    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"

    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"
}

group = "org.boro"
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
    // base services for management, configuration and discovery
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // support web/api
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // google api
    implementation("com.google.api-client:google-api-client:2.0.0")
    implementation("com.google.api-client:google-api-client-gson:2.7.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.36.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.17.0")

    // gmail
    implementation("com.google.apis:google-api-services-gmail:v1-rev20220404-2.0.0")

    // google drive
    implementation("com.google.apis:google-api-services-drive:v3-rev20220815-2.0.0")

    // security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // kotlin support
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation(kotlin("stdlib-jdk8"))

    // testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("com.tngtech.archunit:archunit:1.3.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // developer tools
    compileOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

ktlint {
    debug.set(false)
    verbose.set(false)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    enableExperimentalRules.set(false)
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = FULL
    }
}

// make application executable jar to register it as linux service
tasks.bootJar {
    launchScript()
}
