package org.example.dash.ui.dashboard

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.dash.domain.model.DashLinkDomain
import org.example.dash.domain.model.Result
import org.example.dash.domain.usecase.AddLinkUseCase
import org.example.dash.domain.usecase.GetUserDashboardUseCase
import org.example.dash.ui.base.BaseViewModel
import org.example.dash.utils.UrlOpener
import org.example.dash.utils.getUrlOpener

class DashboardViewModel(
    private val getUserDashboardUseCase: GetUserDashboardUseCase = GetUserDashboardUseCase(),
    private val addLinkUseCase: AddLinkUseCase = AddLinkUseCase(),
    private val lazyUrlOpener: Lazy<UrlOpener> = lazy { getUrlOpener() }
) : BaseViewModel<DashboardState, DashboardIntent>() {


    private val urlOpener by lazyUrlOpener

    private var allLinks: List<DashLinkDomain>? = null
    private var searchJob: Job? = null
    private var initializeJob: Job? = null

    init {
        processIntent(DashboardIntent.Initialize)
    }

    override fun initialState() = DashboardState()

    override fun processIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.Initialize -> initialize()
            is DashboardIntent.SearchQueryEntered -> handleSearch(intent.query)
            is DashboardIntent.LinkClicked -> handleLinkClick(intent.url)
            is DashboardIntent.Retry -> retry()
            is DashboardIntent.OpenAddLinkDialog -> openAddLinkDialog()
            is DashboardIntent.CloseAddLinkDialog -> closeAddLinkDialog()
            is DashboardIntent.UpdateLinkName -> updateLinkName(intent.name)
            is DashboardIntent.UpdateLinkUrl -> updateLinkUrl(intent.url)
            is DashboardIntent.SaveLink -> saveLink()
            is DashboardIntent.DismissAddLinkError -> dismissAddLinkError()
        }
    }

    private fun initialize() {
        val userId = "kshitij"
        val token = "ksh1234"
        
        // Cancel previous initialize job if running
        initializeJob?.cancel()

        updateState { 
            it.copy(
                userId = userId,
                token = token,
                isLoading = true, 
                error = null,
                searchText = ""
            ) 
        }

        initializeJob = viewModelScope.launch {
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

    private fun handleLinkClick(url: String) {
        urlOpener.openUrl(url)
    }

    private fun retry() {
        initialize()
    }
    
    private fun openAddLinkDialog() {
        updateState {
            it.copy(
                addLinkDialogState = AddLinkDialogState()
            )
        }
    }
    
    private fun closeAddLinkDialog() {
        updateState {
            it.copy(
                addLinkDialogState = null
            )
        }
    }
    
    private fun updateLinkName(name: String) {
        updateState {
            it.copy(
                addLinkDialogState = it.addLinkDialogState?.copy(
                    linkName = name,
                    nameError = null
                )
            )
        }
    }
    
    private fun updateLinkUrl(url: String) {
        updateState {
            it.copy(
                addLinkDialogState = it.addLinkDialogState?.copy(
                    linkUrl = url,
                    urlError = null
                )
            )
        }
    }
    
    private fun validateDialogInputs(): Boolean {
        val dialogState = state.value.addLinkDialogState ?: return false
        var isValid = true
        var nameError: String? = null
        var urlError: String? = null
        
        if (dialogState.linkName.isBlank()) {
            nameError = "Link name is required"
            isValid = false
        } else if (dialogState.linkName.length > 100) {
            nameError = "Name too long (max 100 chars)"
            isValid = false
        }
        
        if (dialogState.linkUrl.isBlank()) {
            urlError = "URL is required"
            isValid = false
        } else if (!dialogState.linkUrl.startsWith("http://") && 
                   !dialogState.linkUrl.startsWith("https://")) {
            urlError = "URL must start with http:// or https://"
            isValid = false
        }
        
        if (!isValid) {
            updateState {
                it.copy(
                    addLinkDialogState = dialogState.copy(
                        nameError = nameError,
                        urlError = urlError
                    )
                )
            }
        }
        
        return isValid
    }
    
    private fun saveLink() {
        val currentState = state.value
        val dialogState = currentState.addLinkDialogState ?: return
        val userId = currentState.userId ?: return
        val token = currentState.token ?: return
        
        // Prevent duplicate saves
        if (dialogState.isLoading) return
        
        if (!validateDialogInputs()) return
        
        updateState { 
            it.copy(
                addLinkDialogState = dialogState.copy(
                    isLoading = true,
                    error = null
                )
            ) 
        }
        
        viewModelScope.launch {
            when (val result = addLinkUseCase.invoke(
                userId, 
                token, 
                dialogState.linkName, 
                dialogState.linkUrl
            )) {
                is Result.Success -> {
                    updateState {
                        it.copy(addLinkDialogState = null)
                    }
                    // Refresh the links list
                    initialize()
                }
                is Result.Error -> {
                    updateState {
                        it.copy(
                            addLinkDialogState = dialogState.copy(
                                isLoading = false,
                                error = result.message ?: "Failed to add link"
                            )
                        )
                    }
                }
            }
        }
    }
    
    private fun dismissAddLinkError() {
        val dialogState = state.value.addLinkDialogState ?: return
        updateState { 
            it.copy(
                addLinkDialogState = dialogState.copy(error = null)
            ) 
        }
    }

}