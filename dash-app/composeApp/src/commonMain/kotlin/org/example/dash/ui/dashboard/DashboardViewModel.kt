package org.example.dash.ui.dashboard

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.dash.domain.model.DashLinkDomain
import org.example.dash.domain.model.Result
import org.example.dash.domain.usecase.GetUserDashboardUseCase
import org.example.dash.ui.base.BaseViewModel

class DashboardViewModel(
    private val getUserDashboardUseCase: GetUserDashboardUseCase = GetUserDashboardUseCase()
) : BaseViewModel<DashboardState, DashboardIntent>() {

    private var allLinks: List<DashLinkDomain>? = null
    private var searchJob: Job? = null

    init {
        processIntent(DashboardIntent.Initialize)
    }

    override fun initialState() = DashboardState()

    override fun processIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.Initialize -> initialize()
            is DashboardIntent.SearchQueryEntered -> handleSearch(intent.query)
        }
    }

    private fun initialize() {
        val userId = "kshitij"
        val token = "ksh1234"

        updateState { 
            it.copy(
                userId = userId,
                token = token,
                isLoading = true, 
                error = null,
                searchText = ""
            ) 
        }

        viewModelScope.launch {
            when (val result = getUserDashboardUseCase.invoke(userId, token)) {
                is Result.Success -> {
                    allLinks = result.data.links
                    updateState {
                        it.copy(
                            links = result.data.links,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    updateState {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Something went wrong"
                        )
                    }
                }
            }
        }
    }

    private fun handleSearch(query: String) {
        searchJob?.cancel()
        
        updateState { it.copy(searchText = query) }
        
        searchJob = viewModelScope.launch {
            delay(300)
            
            val filteredLinks = if (query.isEmpty()) {
                allLinks
            } else {
                allLinks?.filter { link ->
                    link.name.contains(query, ignoreCase = true)
                }
            }
            
            updateState { it.copy(links = filteredLinks) }
        }
    }

}