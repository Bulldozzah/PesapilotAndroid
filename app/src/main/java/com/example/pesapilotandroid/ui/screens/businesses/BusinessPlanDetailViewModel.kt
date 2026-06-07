package com.example.pesapilotandroid.ui.screens.businesses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.BusinessPlan
import com.example.pesapilotandroid.data.repository.AuthRepository
import com.example.pesapilotandroid.data.repository.BusinessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BusinessPlanDetailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val businessRepository: BusinessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusinessPlanDetailUiState())
    val uiState: StateFlow<BusinessPlanDetailUiState> = _uiState.asStateFlow()

    private var existingPlanId: String? = null

    fun loadPlan(planId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val userId = authRepository.getCurrentUserId() ?: return@launch

            businessRepository.getBusinessPlans(userId).onSuccess { plans ->
                val plan = plans.find { it.id == planId }
                plan?.let {
                    existingPlanId = it.id
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            businessName = it.title,
                            content = it.content
                        )
                    }
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateBusinessName(value: String) = _uiState.update { it.copy(businessName = value) }
    fun updateContent(value: String) = _uiState.update { it.copy(content = value) }

    fun savePlan() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            val state = _uiState.value

            _uiState.update { it.copy(isSaving = true) }

            val plan = BusinessPlan(
                id = existingPlanId ?: UUID.randomUUID().toString(),
                userId = userId,
                title = state.businessName,
                content = state.content
            )

            val result = if (existingPlanId != null) {
                businessRepository.updateBusinessPlan(plan)
            } else {
                businessRepository.createBusinessPlan(plan)
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, isSaved = true) }
                }
                .onFailure { e ->
                    _uiState.update { 
                        it.copy(isSaving = false, error = e.message ?: "Failed to save plan") 
                    }
                }
        }
    }

    fun deletePlan() {
        viewModelScope.launch {
            existingPlanId?.let { planId ->
                _uiState.update { it.copy(isSaving = true) }
                
                businessRepository.deleteBusinessPlan(planId)
                    .onSuccess {
                        _uiState.update { it.copy(isSaving = false, isSaved = true) }
                    }
                    .onFailure { e ->
                        _uiState.update { 
                            it.copy(isSaving = false, error = e.message ?: "Failed to delete plan") 
                        }
                    }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class BusinessPlanDetailUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val businessName: String = "",
    val content: String = "{}"
)
