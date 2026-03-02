package com.example.plugins

import com.example.api.routes.dashboardRoutes
import com.example.api.routes.healthRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        
        healthRoutes()
        dashboardRoutes()
    }
}
