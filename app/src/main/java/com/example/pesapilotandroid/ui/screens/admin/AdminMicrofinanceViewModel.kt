package com.example.pesapilotandroid.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.Microfinance
import com.example.pesapilotandroid.data.repository.FundingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminMicrofinanceViewModel @Inject constructor(
    private val fundingRepository: FundingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminMicrofinanceUiState())
    val uiState: StateFlow<AdminMicrofinanceUiState> = _uiState.asStateFlow()

    init {
        loadInstitutions()
    }

    private fun loadInstitutions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            fundingRepository.getMicrofinanceInstitutions()
                .onSuccess { institutions ->
                    _uiState.update { 
                        it.copy(
                            institutions = institutions,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun deleteInstitution(id: String) {
        viewModelScope.launch {
            fundingRepository.deleteMicrofinance(id).onSuccess {
                loadInstitutions()
            }
        }
    }
}

data class AdminMicrofinanceUiState(
    val isLoading: Boolean = true,
    val institutions: List<Microfinance> = emptyList()
)
