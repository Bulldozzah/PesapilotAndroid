package com.example.pesapilotandroid.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.UserProfile
import com.example.pesapilotandroid.data.repository.AuthRepository
import com.example.pesapilotandroid.data.repository.BusinessRepository
import com.example.pesapilotandroid.data.repository.PersonalFinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val businessRepository: BusinessRepository,
    private val personalFinanceRepository: PersonalFinanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val userId = authRepository.getCurrentUserId() ?: return@launch

            // Load profile
            authRepository.getUserProfile(userId).onSuccess { profile ->
                _uiState.update { it.copy(profile = profile) }
            }

            // Check admin status
            val isAdmin = authRepository.isAdmin(userId)
            _uiState.update { it.copy(isAdmin = isAdmin) }

            // Load stats
            businessRepository.getUserBusinesses(userId).onSuccess { businesses ->
                _uiState.update { it.copy(businessCount = businesses.size) }
            }

            personalFinanceRepository.getSavingsGoals(userId).onSuccess { goals ->
                _uiState.update { 
                    it.copy(
                        savingsGoalCount = goals.size,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onComplete()
        }
    }
}

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: UserProfile? = null,
    val isAdmin: Boolean = false,
    val businessCount: Int = 0,
    val savingsGoalCount: Int = 0
)
