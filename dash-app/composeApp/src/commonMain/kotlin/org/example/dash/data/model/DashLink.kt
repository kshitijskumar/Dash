package org.example.dash.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DashLink(
    val id: String,
    val name: String,
    val url: String
)
