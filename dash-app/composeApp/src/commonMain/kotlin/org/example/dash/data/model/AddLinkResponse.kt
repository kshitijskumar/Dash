package org.example.dash.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AddLinkResponse(
    val link: DashLink,
    val message: String
)
