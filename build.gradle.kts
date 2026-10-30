plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.9"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.flywaydb.flyway") version "13.11.1"
}

group = "com"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath("org.flywaydb:flyway-database-postgresql:13.11.1")
        classpath("org.postgresql:postgresql:42.7.14")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:4.0.1")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:2.0.1")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("io.github.oshai:kotlin-logging-jvm:8.0.4")

    implementation("org.flywaydb:flyway-database-postgresql:13.11.1")
    implementation("com.github.vertical-blank:sql-formatter:2.0.5")
    runtimeOnly("org.postgresql:postgresql:42.7.14")

    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.mybatis.spring.boot:mybatis-spring-boot-starter-test:4.0.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

data class DatabaseConfig(
    val url: String,
    val user: String,
    val password: String,
    val flywayLocations: List<String>
)

fun loadDatabaseConfig(): DatabaseConfig {
    val activeProfile = project.findProperty("profile")?.toString()
        ?: System.getenv("SPRING_PROFILES_ACTIVE")
        ?: "dev"
    println("Active Profile: $activeProfile")

    val envFile = when (activeProfile) {
        "test" -> file(".env.test")
        else -> file(".env")
    }

    val locationCommon = "filesystem:src/main/resources/db/migration/common"
    val locationDev = "filesystem:src/main/resources/db/migration/dev"

    val flywayLocations = when (activeProfile) {
        "prod", "stage" -> listOf(locationCommon)
        "dev", "test" -> listOf(locationCommon, locationDev)
        else -> error("Unknown profile: $activeProfile")
    }

    var url = ""
    var user = ""
    var password = ""

    if (envFile.exists()) {
        envFile.useLines { lines ->
            lines.map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("#") && it.contains("=") }
                .forEach { line ->
                    val parts = line.split("=", limit = 2)
                    val key = parts[0].trim()
                    val value = parts[1].trim().removeSurrounding("\"").removeSurrounding("'")

                    when (key) {
                        "DB_URL" -> url = value
                        "DB_USERNAME" -> user = value
                        "DB_PASSWORD" -> password = value
                    }
                }
        }
    }

    url = System.getenv("DB_URL") ?: url
    user = System.getenv("DB_USERNAME") ?: user
    password = System.getenv("DB_PASSWORD") ?: password

    return DatabaseConfig(url, user, password, flywayLocations)
}

val (dbUrl, dbUser, dbPassword, flywayLocations) = loadDatabaseConfig()

flyway {
    url = dbUrl
    user = dbUser
    password = dbPassword

    locations = flywayLocations.toTypedArray()
}

tasks.matching { it.name.startsWith("flyway") }.configureEach {
    doFirst {
        require(dbUrl.isNotBlank()) { "DB_URL is required" }
        require(dbUser.isNotBlank()) { "DB_USERNAME is required" }
        require(dbPassword.isNotBlank()) { "DB_PASSWORD is required" }
    }
}

tasks.jar {
    enabled = false
}

tasks.withType<Test> {
    useJUnitPlatform()
}
