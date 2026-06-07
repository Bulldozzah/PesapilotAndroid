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
class MicrofinanceViewModel @Inject constructor(
    private val fundingRepository: FundingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MicrofinanceUiState())
    val uiState: StateFlow<MicrofinanceUiState> = _uiState.asStateFlow()

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
}

data class MicrofinanceUiState(
    val isLoading: Boolean = true,
    val institutions: List<Microfinance> = emptyList()
)
