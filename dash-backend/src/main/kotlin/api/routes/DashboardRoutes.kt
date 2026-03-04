package com.example.api.routes

import com.example.api.dto.AddLinkRequest
import com.example.api.dto.AddLinkResponse
import com.example.api.dto.DashboardRequestModel
import com.example.api.dto.ErrorResponse
import com.example.config.DependencyInjection
import com.example.domain.exceptions.DashboardNotFoundException
import com.example.domain.exceptions.InvalidUrlException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
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
    
    post("/dashls/{userId}/{token}/link") {
        val userId = call.parameters["userId"]
        val token = call.parameters["token"]
        
        if (userId.isNullOrBlank()) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(error = "userId is required")
            )
            return@post
        }
        
        if (token.isNullOrBlank()) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(error = "token is required")
            )
            return@post
        }
        
        try {
            val request = call.receive<AddLinkRequest>()
            val dashboardService = DependencyInjection.getDashboardService()
            val link = dashboardService.addLink(userId, token, request)
            
            call.respond(
                HttpStatusCode.Created,
                AddLinkResponse(
                    link = link,
                    message = "Link added successfully"
                )
            )
        } catch (e: DashboardNotFoundException) {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    error = "No data found for the provided userId and token",
                    message = e.message
                )
            )
        } catch (e: InvalidUrlException) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    error = e.message ?: "Invalid URL format",
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
                    error = "Failed to add link",
                    message = e.message
                )
            )
        }
    }
}
