package com.example

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*

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
        
        get("/all") {
            if (!FirebaseService.isInitialized()) {
                call.respond(
                    HttpStatusCode.ServiceUnavailable,
                    mapOf("error" to "Firebase is not initialized")
                )
                return@get
            }
            
            try {
                val firestore = FirebaseService.getFirestore()
                
                val documents = withContext(Dispatchers.IO) {
                    val querySnapshot = firestore.collection("data").get().get()
                    querySnapshot.documents.map { doc ->
                        buildJsonObject {
                            put("id", doc.id)
                            putJsonObject("data") {
                                doc.data.forEach { (key, value) ->
                                    put(key, convertToJsonElement(value))
                                }
                            }
                        }
                    }
                }
                
                val response = buildJsonObject {
                    put("collection", "data")
                    put("count", documents.size)
                    putJsonArray("documents") {
                        documents.forEach { add(it) }
                    }
                }
                
                call.respondText(response.toString(), ContentType.Application.Json, HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "error" to "Failed to fetch data",
                        "message" to e.message
                    )
                )
            }
        }
        
        get("/dashls/{userId}/{token}") {
            if (!FirebaseService.isInitialized()) {
                call.respond(
                    HttpStatusCode.ServiceUnavailable,
                    mapOf("error" to "Firebase is not initialized")
                )
                return@get
            }
            
            val userId = call.parameters["userId"]
            val token = call.parameters["token"]
            
            if (userId.isNullOrBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "userId is required")
                )
                return@get
            }
            
            if (token.isNullOrBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "token is required")
                )
                return@get
            }
            
            try {
                val firestore = FirebaseService.getFirestore()
                
                val result = withContext(Dispatchers.IO) {
                    val querySnapshot = firestore.collection("data")
                        .whereEqualTo("userId", userId)
                        .whereEqualTo("token", token)
                        .limit(1)
                        .get()
                        .get()
                    
                    if (querySnapshot.isEmpty) {
                        null
                    } else {
                        val doc = querySnapshot.documents[0]
                        val data = doc.data
                        
                        buildJsonObject {
                            put("userId", data["userId"]?.toString() ?: "")
                            
                            val linksValue = data["links"]
                            if (linksValue != null) {
                                put("links", convertToJsonElement(linksValue))
                            } else {
                                putJsonArray("links") {}
                            }
                        }
                    }
                }
                
                if (result != null) {
                    call.respondText(result.toString(), ContentType.Application.Json, HttpStatusCode.OK)
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "No data found for the provided userId and token")
                    )
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "error" to "Failed to fetch data",
                        "message" to e.message
                    )
                )
            }
        }
    }
}

private fun convertToJsonElement(value: Any?): JsonElement {
    return when (value) {
        null -> JsonNull
        is String -> JsonPrimitive(value)
        is Number -> JsonPrimitive(value)
        is Boolean -> JsonPrimitive(value)
        is List<*> -> buildJsonArray {
            value.forEach { add(convertToJsonElement(it)) }
        }
        is Map<*, *> -> buildJsonObject {
            value.forEach { (k, v) ->
                put(k.toString(), convertToJsonElement(v))
            }
        }
        else -> JsonPrimitive(value.toString())
    }
}

