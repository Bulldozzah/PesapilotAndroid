package com.example.pesapilotandroid.ui.screens.money

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.SavingsGoal
import com.example.pesapilotandroid.data.repository.AuthRepository
import com.example.pesapilotandroid.data.repository.PersonalFinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SavingsGoalsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val personalFinanceRepository: PersonalFinanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavingsGoalsUiState())
    val uiState: StateFlow<SavingsGoalsUiState> = _uiState.asStateFlow()

    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val userId = authRepository.getCurrentUserId() ?: return@launch

            authRepository.getUserProfile(userId).onSuccess { profile ->
                _uiState.update { it.copy(currency = profile?.currency ?: "USD") }
            }

            personalFinanceRepository.getSavingsGoals(userId)
                .onSuccess { goals ->
                    _uiState.update { 
                        it.copy(
                            goals = goals,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun createGoal(name: String, targetAmount: Double) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch

            val goal = SavingsGoal(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = name,
                targetAmount = targetAmount,
                currentAmount = 0.0
            )

            personalFinanceRepository.createSavingsGoal(goal).onSuccess {
                loadGoals()
            }
        }
    }

    fun addFunds(goalId: String, amount: Double) {
        viewModelScope.launch {
            val goal = _uiState.value.goals.find { it.id == goalId } ?: return@launch
            
            val updatedGoal = goal.copy(
                currentAmount = goal.currentAmount + amount
            )

            personalFinanceRepository.updateSavingsGoal(updatedGoal).onSuccess {
                loadGoals()
            }
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            personalFinanceRepository.deleteSavingsGoal(goalId).onSuccess {
                loadGoals()
            }
        }
    }
}

data class SavingsGoalsUiState(
    val isLoading: Boolean = true,
    val currency: String = "USD",
    val goals: List<SavingsGoal> = emptyList()
)
