package com.example.api.routes

import com.example.api.dto.DashboardRequestModel
import com.example.api.dto.ErrorResponse
import com.example.config.DependencyInjection
import com.example.domain.exceptions.DashboardNotFoundException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.dashboardRoutes() {
    
    get("/all") {
        try {
            val dashboardService = DependencyInjection.getDashboardService()
            val response = dashboardService.getAllData()
            call.respond(HttpStatusCode.OK, response)
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    error = "Failed to fetch data",
                    message = e.message
                )
            )
        }
    }
    
    get("/dashls/{userId}/{token}") {
        val userId = call.parameters["userId"]
        val token = call.parameters["token"]
        
        if (userId.isNullOrBlank()) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(error = "userId is required")
            )
            return@get
        }
        
        if (token.isNullOrBlank()) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(error = "token is required")
            )
            return@get
        }
        
        try {
            val request = DashboardRequestModel(userId = userId, token = token)
            val dashboardService = DependencyInjection.getDashboardService()
            val response = dashboardService.getUserDashboard(request)
            call.respond(HttpStatusCode.OK, response)
        } catch (e: DashboardNotFoundException) {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    error = "No data found for the provided userId and token",
                    message = e.message
                )
            )
        } catch (e: IllegalArgumentException) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(error = e.message ?: "Invalid request")
            )
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    error = "Failed to fetch data",
                    message = e.message
                )
            )
        }
    }
}
