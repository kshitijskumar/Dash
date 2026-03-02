package com.example

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        
        get("/health") {
            if (FirebaseService.isInitialized()) {
                try {
                    val firestore = FirebaseService.getFirestore()
                    call.respond(mapOf(
                        "status" to "healthy",
                        "firebase" to "initialized"
                    ))
                } catch (e: Exception) {
                    call.respond(mapOf(
                        "status" to "unhealthy",
                        "firebase" to "error",
                        "error" to e.message
                    ))
                }
            } else {
                call.respond(mapOf(
                    "status" to "healthy",
                    "firebase" to "not initialized"
                ))
            }
        }
    }
}

