package com.example.api.utils

import kotlinx.serialization.json.*

object JsonConverter {
    fun convertToJsonElement(value: Any?): JsonElement {
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
    
    fun convertToMap(data: Map<String, Any>): Map<String, JsonElement> {
        return data.mapValues { (_, value) -> convertToJsonElement(value) }
    }
}
