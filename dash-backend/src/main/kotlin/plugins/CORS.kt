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
        val allowedOrigins = System.getenv("ALLOWED_ORIGINS")
            ?.split(",")
            ?.map { it.trim() }
            ?: listOf(
                "http://localhost:8081",
                "http://localhost:8080"
            )
        
        // Parse and allow each origin
        allowedOrigins.forEach { origin ->
            try {
                // Use Ktor's URL parser
                val url = Url(origin)
                allowHost(
                    host = url.host,
                    schemes = listOf(url.protocol.name),
                    subDomains = emptyList()
                )
                
//                log.info("CORS: Allowed origin - $origin")
            } catch (e: Exception) {
//                log.error("CORS: Failed to parse origin - $origin", e)
            }
        }
        
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }
}
