import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version Versions.SPRING_BOOT_VERSION
    id("io.spring.dependency-management") version Versions.SPRING_DEPENDENCY_MANAGEMENT

    kotlin("jvm") version Versions.KOTLIN_VERSION
    kotlin("kapt") version Versions.KOTLIN_VERSION
    kotlin("plugin.spring") version Versions.KOTLIN_VERSION

    `java-library`
    `maven-publish`
}

group = "com.briolink.lib"
version = "0.2.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
//    mavenLocal()
    maven {
        url = uri("https://gitlab.com/api/v4/projects/29889174/packages/maven")
        authentication {
            create<HttpHeaderAuthentication>("header")
        }
        credentials(HttpHeaderCredentials::class) {
            name = "Deploy-Token"
            value = System.getenv("CI_DEPLOY_PASSWORD")
        }
    }
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.springframework.boot:spring-boot-autoconfigure-processor:${Versions.SPRING_BOOT_VERSION}")
    kapt("org.springframework.boot:spring-boot-configuration-processor:${Versions.SPRING_BOOT_VERSION}")

    // FasterXML
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    kapt("jakarta.annotation:jakarta.annotation-api")

    // Kotlin
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))

    // kotlin-logging
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")

    implementation("com.briolink.lib:event:${Versions.BRIOLINK_EVENT}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitLab"
            url = uri("https://gitlab.com/api/v4/projects/33688770/packages/maven")

            authentication {
                create<HttpHeaderAuthentication>("header")
            }

            credentials(HttpHeaderCredentials::class) {
                name = "Deploy-Token"
                value = System.getenv("GITLAB_DEPLOY_TOKEN")
            }
        }
        mavenLocal()
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
