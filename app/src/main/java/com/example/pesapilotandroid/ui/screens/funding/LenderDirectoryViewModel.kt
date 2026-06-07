package com.example.pesapilotandroid.ui.screens.funding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.Lender
import com.example.pesapilotandroid.data.repository.FundingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LenderDirectoryViewModel @Inject constructor(
    private val fundingRepository: FundingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LenderDirectoryUiState())
    val uiState: StateFlow<LenderDirectoryUiState> = _uiState.asStateFlow()

    init {
        loadLenders()
    }

    private fun loadLenders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            fundingRepository.getLenders()
                .onSuccess { lenders ->
                    _uiState.update { 
                        it.copy(
                            lenders = lenders,
                            filteredLenders = lenders,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun filterByType(type: String?) {
        _uiState.update { state ->
            val filtered = if (type == null) {
                state.lenders
            } else {
                state.lenders.filter { it.lenderType == type }
            }
            state.copy(
                selectedType = type,
                filteredLenders = filtered
            )
        }
    }
}

data class LenderDirectoryUiState(
    val isLoading: Boolean = true,
    val lenders: List<Lender> = emptyList(),
    val filteredLenders: List<Lender> = emptyList(),
    val selectedType: String? = null
)
