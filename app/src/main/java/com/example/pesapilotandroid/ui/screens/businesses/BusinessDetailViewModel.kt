package com.example.pesapilotandroid.ui.screens.businesses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.StepProgress
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
class BusinessDetailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val businessRepository: BusinessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusinessDetailUiState())
    val uiState: StateFlow<BusinessDetailUiState> = _uiState.asStateFlow()

    fun loadBusiness(businessId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val userId = authRepository.getCurrentUserId() ?: return@launch

            // Get user profile for country
            authRepository.getUserProfile(userId).onSuccess { profile ->
                profile?.let {
                    // Get country authority
                    val countryCode = it.countryCode ?: "KE"
                    businessRepository.getCountryAuthority(countryCode).onSuccess { authority ->
                        authority?.let { auth ->
                            _uiState.update { state ->
                                state.copy(
                                    countryAuthority = CountryAuthorityInfo(
                                        countryName = auth.countryName,
                                        authorityName = auth.authorityName,
                                        website = auth.authorityWebsite
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Get user businesses and find the one with matching ID
            businessRepository.getUserBusinesses(userId).onSuccess { businesses ->
                val business = businesses.find { it.id == businessId }
                _uiState.update { 
                    it.copy(
                        business = business,
                        isLoading = false
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun completeStep(stepNumber: Int) {
        viewModelScope.launch {
            val business = _uiState.value.business ?: return@launch
            
            // Progress is now tracked via step_progress table
            // For now, just mark the step as complete
            val progress = StepProgress(
                userBusinessId = business.id,
                stepNumber = stepNumber,
                completed = true,
                completedAt = java.time.Instant.now().toString()
            )
            
            businessRepository.updateStepProgress(progress).onSuccess {
                // Reload business data
                loadBusiness(business.id)
            }
        }
    }
}

data class BusinessDetailUiState(
    val isLoading: Boolean = true,
    val business: UserBusiness? = null,
    val countryAuthority: CountryAuthorityInfo? = null
)
