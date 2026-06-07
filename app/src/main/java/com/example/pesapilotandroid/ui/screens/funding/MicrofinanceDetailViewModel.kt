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
class MicrofinanceDetailViewModel @Inject constructor(
    private val fundingRepository: FundingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MicrofinanceDetailUiState())
    val uiState: StateFlow<MicrofinanceDetailUiState> = _uiState.asStateFlow()

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
}

data class MicrofinanceDetailUiState(
    val isLoading: Boolean = true,
    val microfinance: Microfinance? = null
)
