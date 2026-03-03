package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCORS() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        
        // Get allowed origins from environment variable or use localhost defaults
        val allowedOriginsString = System.getenv("ALLOWED_ORIGINS")
        
        if (allowedOriginsString.isNullOrBlank()) {
            // Development mode - allow localhost with any port
            environment.log.info("CORS: Development mode - allowing localhost")
            allowHost("localhost", schemes = listOf("http", "https"))
            allowHost("127.0.0.1", schemes = listOf("http", "https"))
        } else {
            // Production mode - only allow specified origins
            val allowedOrigins = allowedOriginsString.split(",").map { it.trim() }
            allowedOrigins.forEach { origin ->
                try {
                    val url = Url(origin)
                    allowHost(url.host, schemes = listOf(url.protocol.name))
                    environment.log.info("CORS: Allowed origin - $origin")
                } catch (e: Exception) {
                    environment.log.error("CORS: Failed to parse origin - $origin", e)
                }
            }
        }
        
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }
}
