package com.example.pesapilotandroid.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.repository.BusinessRepository
import com.example.pesapilotandroid.data.repository.FundingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminOverviewViewModel @Inject constructor(
    private val businessRepository: BusinessRepository,
    private val fundingRepository: FundingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminOverviewUiState())
    val uiState: StateFlow<AdminOverviewUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Load business templates count
            businessRepository.getBusinessTemplates().onSuccess { templates ->
                _uiState.update { it.copy(templateCount = templates.size) }
            }

            // Load categories count
            businessRepository.getBusinessCategories().onSuccess { categories ->
                _uiState.update { it.copy(categoryCount = categories.size) }
            }

            // Load lenders count
            fundingRepository.getLenders().onSuccess { lenders ->
                _uiState.update { it.copy(lenderCount = lenders.size) }
            }

            // Load microfinance count
            fundingRepository.getMicrofinanceInstitutions().onSuccess { mfis ->
                _uiState.update { 
                    it.copy(
                        microfinanceCount = mfis.size,
                        isLoading = false
                    )
                }
            }
        }
    }
}

data class AdminOverviewUiState(
    val isLoading: Boolean = true,
    val templateCount: Int = 0,
    val categoryCount: Int = 0,
    val lenderCount: Int = 0,
    val microfinanceCount: Int = 0
)
