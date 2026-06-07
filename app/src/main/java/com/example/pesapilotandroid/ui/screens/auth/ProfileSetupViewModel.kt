package com.example.pesapilotandroid.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.UserProfile
import com.example.pesapilotandroid.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    init {
        loadExistingProfile()
    }

    private fun loadExistingProfile() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            authRepository.getUserProfile(userId).onSuccess { profile ->
                profile?.let {
                    _uiState.update { state ->
                        state.copy(
                            fullName = it.fullName,
                            phoneNumber = it.phone ?: "",
                            country = it.country,
                            countryCode = it.countryCode ?: "",
                            currency = it.currency,
                            businessName = it.businessName ?: ""
                        )
                    }
                }
            }
        }
    }

    fun updateFullName(name: String) {
        _uiState.update { it.copy(fullName = name) }
    }

    fun updatePhoneNumber(phone: String) {
        _uiState.update { it.copy(phoneNumber = phone) }
    }

    fun updateCountry(country: Country) {
        _uiState.update { 
            it.copy(
                country = country.name,
                countryCode = country.code,
                currency = country.currency
            )
        }
    }

    fun updateCurrency(currency: String) {
        _uiState.update { it.copy(currency = currency) }
    }

    fun updateBusinessName(name: String) {
        _uiState.update { it.copy(businessName = name) }
    }

    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, error = "User not authenticated") }
                return@launch
            }

            val state = _uiState.value
            val profile = UserProfile(
                id = userId, // Profile ID is same as user ID
                fullName = state.fullName,
                country = state.country,
                countryCode = state.countryCode.ifBlank { null },
                currency = state.currency,
                phone = state.phoneNumber.ifBlank { null },
                businessName = state.businessName.ifBlank { null },
                completedOnboarding = true
            )

            // Check if profile exists
            val existingProfile = authRepository.getUserProfile(userId).getOrNull()
            
            val result = if (existingProfile != null) {
                authRepository.updateUserProfile(profile.copy(id = existingProfile.id))
            } else {
                authRepository.createUserProfile(profile)
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isComplete = true) }
                }
                .onFailure { e ->
                    _uiState.update { 
                        it.copy(isLoading = false, error = e.message ?: "Failed to save profile") 
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class ProfileSetupUiState(
    val fullName: String = "",
    val phoneNumber: String = "",
    val country: String = "",
    val countryCode: String = "",
    val currency: String = "",
    val businessName: String = "",
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
    val error: String? = null
)
