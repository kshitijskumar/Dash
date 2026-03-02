package com.example.api.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class AllDataResponse(
    val collection: String,
    val count: Int,
    val documents: List<DocumentData>
)

@Serializable
data class DocumentData(
    val id: String,
    val data: Map<String, JsonElement>
)
