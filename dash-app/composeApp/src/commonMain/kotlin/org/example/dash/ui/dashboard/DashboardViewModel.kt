package org.example.dash.ui.dashboard

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.example.dash.domain.model.Result
import org.example.dash.domain.usecase.GetUserDashboardUseCase
import org.example.dash.ui.base.BaseViewModel

class DashboardViewModel(
    private val getUserDashboardUseCase: GetUserDashboardUseCase = GetUserDashboardUseCase()
) : BaseViewModel<DashboardState, DashboardIntent>() {

    init {
        processIntent(DashboardIntent.Initialize)
    }

    override fun initialState() = DashboardState()

    override fun processIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.Initialize -> initialize()
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

}