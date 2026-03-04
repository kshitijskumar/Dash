package org.example.dash.ui.dashboard

import org.example.dash.domain.model.DashLinkDomain

data class DashboardState(
    val userId: String? = null,
    val token: String? = null,
    val searchText: String = "",
    val links: List<DashLinkDomain>? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val addLinkDialogState: AddLinkDialogState? = null
)

data class AddLinkDialogState(
    val linkName: String = "",
    val linkUrl: String = "",
    val nameError: String? = null,
    val urlError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class DashboardIntent {
    data object Initialize : DashboardIntent()
    data class SearchQueryEntered(val query: String) : DashboardIntent()
    data class LinkClicked(val url: String) : DashboardIntent()
    data object Retry : DashboardIntent()
    data object OpenAddLinkDialog : DashboardIntent()
    data object CloseAddLinkDialog : DashboardIntent()
    data class UpdateLinkName(val name: String) : DashboardIntent()
    data class UpdateLinkUrl(val url: String) : DashboardIntent()
    data object SaveLink : DashboardIntent()
    data object DismissAddLinkError : DashboardIntent()
}