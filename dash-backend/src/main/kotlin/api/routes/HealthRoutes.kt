package com.example.api.routes

import com.example.api.dto.HealthResponse
import com.example.config.FirebaseService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.healthRoutes() {
    get("/health") {
        if (FirebaseService.isInitialized()) {
            try {
                FirebaseService.getFirestore()
                call.respond(
                    HttpStatusCode.OK,
                    HealthResponse(status = "healthy", firebase = "initialized")
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.OK,
                    HealthResponse(status = "unhealthy", firebase = "error: ${e.message}")
                )
            }
        } else {
            call.respond(
                HttpStatusCode.OK,
                HealthResponse(status = "healthy", firebase = "not initialized")
            )
        }
    }
}
