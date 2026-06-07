package com.example.pesapilotandroid.ui.screens.profile

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
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private var existingProfile: UserProfile? = null

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val userId = authRepository.getCurrentUserId() ?: return@launch

            authRepository.getUserProfile(userId).onSuccess { profile ->
                existingProfile = profile
                profile?.let {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            fullName = it.fullName,
                            phoneNumber = it.phone ?: "",
                            country = it.country,
                            countryCode = it.countryCode ?: "",
                            currency = it.currency,
                            businessName = it.businessName ?: ""
                        )
                    }
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateFullName(value: String) = _uiState.update { it.copy(fullName = value) }
    fun updatePhoneNumber(value: String) = _uiState.update { it.copy(phoneNumber = value) }
    fun updateCountry(name: String, code: String) = _uiState.update { 
        it.copy(country = name, countryCode = code) 
    }
    fun updateCurrency(value: String) = _uiState.update { it.copy(currency = value) }
    fun updateBusinessName(value: String) = _uiState.update { it.copy(businessName = value) }

    fun saveProfile() {
        viewModelScope.launch {
            val state = _uiState.value
            val existing = existingProfile ?: return@launch

            _uiState.update { it.copy(isSaving = true) }

            val updatedProfile = existing.copy(
                fullName = state.fullName,
                phone = state.phoneNumber.ifBlank { null },
                country = state.country,
                countryCode = state.countryCode.ifBlank { null },
                currency = state.currency,
                businessName = state.businessName.ifBlank { null }
            )

            authRepository.updateUserProfile(updatedProfile)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, isSaved = true) }
                }
                .onFailure { e ->
                    _uiState.update { 
                        it.copy(isSaving = false, error = e.message ?: "Failed to save profile") 
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class EditProfileUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val fullName: String = "",
    val phoneNumber: String = "",
    val country: String = "",
    val countryCode: String = "",
    val currency: String = "",
    val businessName: String = ""
)
