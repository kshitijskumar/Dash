package com.example.domain.models

data class DashboardData(
    val id: String,
    val userId: String,
    val token: String,
    val links: List<Link>
)
