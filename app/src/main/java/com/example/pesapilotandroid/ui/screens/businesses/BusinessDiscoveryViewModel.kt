package com.example.pesapilotandroid.ui.screens.businesses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.BusinessCategory
import com.example.pesapilotandroid.data.model.BusinessTemplate
import com.example.pesapilotandroid.data.repository.BusinessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusinessDiscoveryViewModel @Inject constructor(
    private val businessRepository: BusinessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusinessDiscoveryUiState())
    val uiState: StateFlow<BusinessDiscoveryUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Load categories
            businessRepository.getBusinessCategories().onSuccess { categories ->
                _uiState.update { it.copy(categories = categories) }
            }

            // Load templates
            businessRepository.getBusinessTemplates().onSuccess { templates ->
                _uiState.update { 
                    it.copy(
                        templates = templates,
                        filteredTemplates = templates,
                        isLoading = false
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun selectCategory(category: BusinessCategory?) {
        _uiState.update { state ->
            val filtered = if (category == null) {
                state.templates
            } else {
                state.templates.filter { it.categoryId == category.id }
            }
            state.copy(
                selectedCategory = category,
                filteredTemplates = filtered
            )
        }
    }
}

data class BusinessDiscoveryUiState(
    val isLoading: Boolean = true,
    val categories: List<BusinessCategory> = emptyList(),
    val templates: List<BusinessTemplate> = emptyList(),
    val filteredTemplates: List<BusinessTemplate> = emptyList(),
    val selectedCategory: BusinessCategory? = null
)
