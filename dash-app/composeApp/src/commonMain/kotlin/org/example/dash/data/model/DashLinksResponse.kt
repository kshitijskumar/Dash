package org.example.dash.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DashLinksResponse(
    val userId: String,
    val links: List<DashLink>
)
