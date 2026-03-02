package com.example.plugins

import com.example.api.dto.ErrorResponse
import com.example.config.FirebaseService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.response.*

val FirebaseInitializationPlugin = createApplicationPlugin(
    name = "FirebaseInitializationPlugin"
) {
    on(CallSetup) { call ->
        val path = call.request.local.uri
        
        val requiresFirebase = path.startsWith("/all") || 
                              path.startsWith("/dashls")
        
        if (requiresFirebase && !FirebaseService.isInitialized()) {
            call.respond(
                HttpStatusCode.ServiceUnavailable,
                ErrorResponse(error = "Firebase is not initialized")
            )
        }
    }
}

fun Application.configureFirebasePlugin() {
    install(FirebaseInitializationPlugin)
}
