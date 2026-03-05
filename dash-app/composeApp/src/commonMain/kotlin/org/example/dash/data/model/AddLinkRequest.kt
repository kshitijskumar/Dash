package org.example.dash.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AddLinkRequest(
    val name: String,
    val url: String
)
