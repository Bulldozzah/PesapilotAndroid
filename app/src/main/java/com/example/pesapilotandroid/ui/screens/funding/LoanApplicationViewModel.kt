package com.example.pesapilotandroid.ui.screens.funding

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
class LoanApplicationViewModel @Inject constructor(
    private val fundingRepository: FundingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoanApplicationUiState())
    val uiState: StateFlow<LoanApplicationUiState> = _uiState.asStateFlow()

    fun loadMicrofinance(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            fundingRepository.getMicrofinanceById(id)
                .onSuccess { microfinance ->
                    _uiState.update { 
                        it.copy(
                            microfinance = microfinance,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun simulateUpload(documentType: String) {
        // In a real app, this would handle actual file upload
        // For demo purposes, we simulate a successful upload
        _uiState.update { state ->
            val newDocs = state.uploadedDocuments.toMutableMap()
            newDocs[documentType] = "https://example.com/docs/$documentType"
            state.copy(uploadedDocuments = newDocs)
        }
    }

    fun submitApplication(method: String) {
        _uiState.update { it.copy(applicationReady = true) }
    }
}

data class LoanApplicationUiState(
    val isLoading: Boolean = true,
    val microfinance: Microfinance? = null,
    val uploadedDocuments: Map<String, String> = emptyMap(),
    val applicationReady: Boolean = false
)
