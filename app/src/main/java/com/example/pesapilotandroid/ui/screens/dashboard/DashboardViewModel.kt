package com.example.pesapilotandroid.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.SavingsGoal
import com.example.pesapilotandroid.data.model.UserBusiness
import com.example.pesapilotandroid.data.repository.AuthRepository
import com.example.pesapilotandroid.data.repository.BusinessRepository
import com.example.pesapilotandroid.data.repository.PersonalFinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val businessRepository: BusinessRepository,
    private val personalFinanceRepository: PersonalFinanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val userId = authRepository.getCurrentUserId() ?: return@launch

            // Load user profile
            authRepository.getUserProfile(userId).onSuccess { profile ->
                _uiState.update { 
                    it.copy(
                        userName = profile?.fullName ?: "",
                        currency = profile?.currency ?: "USD"
                    )
                }
            }

            // Load businesses
            businessRepository.getUserBusinesses(userId).onSuccess { businesses ->
                _uiState.update { it.copy(activeBusinesses = businesses) }
            }

            // Load savings goals
            personalFinanceRepository.getSavingsGoals(userId).onSuccess { goals ->
                _uiState.update { it.copy(savingsGoals = goals) }
            }

            // Load monthly finance summary
            val now = LocalDate.now()
            val summary = personalFinanceRepository.getMonthlyFinanceSummary(
                userId = userId,
                month = now.monthValue,
                year = now.year
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    monthlyIncome = summary.totalIncome,
                    monthlyExpenses = summary.totalExpenses,
                    netSavings = summary.netSavings
                )
            }
        }
    }

    fun refresh() {
        loadDashboardData()
    }
}

data class DashboardUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val currency: String = "USD",
    val monthlyIncome: Double = 0.0,
    val monthlyExpenses: Double = 0.0,
    val netSavings: Double = 0.0,
    val activeBusinesses: List<UserBusiness> = emptyList(),
    val savingsGoals: List<SavingsGoal> = emptyList()
)
