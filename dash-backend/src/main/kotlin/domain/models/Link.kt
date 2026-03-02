package com.example.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Link(
    val id: String,
    val name: String,
    val url: String
)
