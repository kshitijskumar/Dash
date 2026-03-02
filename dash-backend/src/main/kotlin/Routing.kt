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

