package com.example.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class AddLinkRequest(
    val name: String,
    val url: String
) {
    init {
        require(name.isNotBlank()) { "Link name cannot be blank" }
        require(name.length <= 100) { "Link name cannot exceed 100 characters" }
        require(url.isNotBlank()) { "URL cannot be blank" }
    }
}
