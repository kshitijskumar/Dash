package com.example.api.dto

import com.example.domain.models.Link
import kotlinx.serialization.Serializable

@Serializable
data class DashboardResponseModel(
    val userId: String,
    val links: List<Link>
)
