plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(17)
}

ktor {
    fatJar {
        archiveFileName.set("app.jar")
    }
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    // gRPC/Google client libraries rely on META-INF/services entries.
    // In a fat JAR, these files must be merged instead of dropped.
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    mergeServiceFiles()
}

dependencies {
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.firebase.admin)
    implementation(libs.grpc.netty.shaded)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}

configurations.all {
    resolutionStrategy {
        force(libs.grpc.netty.shaded)
    }
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "io.ktor.server.netty.EngineMain"
    }
}

