package org.example.dash.domain.model

data class UserDashboard(
    val userId: String,
    val links: List<DashLinkDomain>
)
