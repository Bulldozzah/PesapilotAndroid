package com.example.pesapilotandroid.ui.screens.businesses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.UserBusiness
import com.example.pesapilotandroid.data.repository.AuthRepository
import com.example.pesapilotandroid.data.repository.BusinessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyBusinessesViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val businessRepository: BusinessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyBusinessesUiState())
    val uiState: StateFlow<MyBusinessesUiState> = _uiState.asStateFlow()

    init {
        loadBusinesses()
    }

    private fun loadBusinesses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val userId = authRepository.getCurrentUserId() ?: return@launch

            businessRepository.getUserBusinesses(userId)
                .onSuccess { businesses ->
                    _uiState.update { 
                        it.copy(
                            businesses = businesses,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun refresh() {
        loadBusinesses()
    }
}

data class MyBusinessesUiState(
    val isLoading: Boolean = true,
    val businesses: List<UserBusiness> = emptyList()
)
