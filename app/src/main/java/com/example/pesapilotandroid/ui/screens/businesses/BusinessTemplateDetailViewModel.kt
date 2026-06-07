package com.example.pesapilotandroid.ui.screens.businesses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.BusinessTemplate
import com.example.pesapilotandroid.data.model.BusinessTemplateStep
import com.example.pesapilotandroid.data.model.UserBusiness
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
class BusinessTemplateDetailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val businessRepository: BusinessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusinessTemplateDetailUiState())
    val uiState: StateFlow<BusinessTemplateDetailUiState> = _uiState.asStateFlow()

    fun loadTemplate(templateId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Load template
            businessRepository.getBusinessTemplates().onSuccess { templates ->
                val template = templates.find { it.id == templateId }
                _uiState.update { it.copy(template = template) }
            }

            // Load steps
            businessRepository.getBusinessTemplateSteps(templateId).onSuccess { steps ->
                _uiState.update { 
                    it.copy(
                        steps = steps,
                        isLoading = false
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun startBusiness() {
        viewModelScope.launch {
            val template = _uiState.value.template ?: return@launch
            val userId = authRepository.getCurrentUserId() ?: return@launch

            _uiState.update { it.copy(isCreating = true) }

            val newBusiness = UserBusiness(
                id = UUID.randomUUID().toString(),
                userId = userId,
                templateId = template.id,
                name = template.name,
                description = template.description,
                currency = template.currency,
                budget = template.startupCostMin
            )

            businessRepository.createUserBusiness(newBusiness)
                .onSuccess { created ->
                    _uiState.update { 
                        it.copy(
                            isCreating = false,
                            createdBusinessId = created.id
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isCreating = false) }
                }
        }
    }
}

data class BusinessTemplateDetailUiState(
    val isLoading: Boolean = true,
    val isCreating: Boolean = false,
    val template: BusinessTemplate? = null,
    val steps: List<BusinessTemplateStep> = emptyList(),
    val createdBusinessId: String? = null
)
