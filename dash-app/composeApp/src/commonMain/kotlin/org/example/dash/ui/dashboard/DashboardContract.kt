package org.example.dash.ui.dashboard

import org.example.dash.domain.model.DashLinkDomain

data class DashboardState(
    val userId: String? = null,
    val token: String? = null,
    val searchText: String = "",
    val links: List<DashLinkDomain>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class DashboardIntent {
    data object Initialize : DashboardIntent()
}