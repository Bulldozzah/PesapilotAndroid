package com.example.pesapilotandroid.ui.screens.money

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.MonthlyCarryover
import com.example.pesapilotandroid.data.repository.AuthRepository
import com.example.pesapilotandroid.data.repository.PersonalFinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WalletPlannerViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val personalFinanceRepository: PersonalFinanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletPlannerUiState())
    val uiState: StateFlow<WalletPlannerUiState> = _uiState.asStateFlow()

    init {
        val now = LocalDate.now()
        _uiState.update { 
            it.copy(
                selectedMonth = now.monthValue,
                selectedYear = now.year
            )
        }
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val userId = authRepository.getCurrentUserId() ?: return@launch
            val state = _uiState.value

            // Get currency from profile
            authRepository.getUserProfile(userId).onSuccess { profile ->
                _uiState.update { it.copy(currency = profile?.currency ?: "USD") }
            }

            // Get current month summary
            val summary = personalFinanceRepository.getMonthlyFinanceSummary(
                userId = userId,
                month = state.selectedMonth,
                year = state.selectedYear
            )

            // Check for carryover
            val carryover = personalFinanceRepository.getMonthlyCarryover(
                userId = userId,
                toMonth = state.selectedMonth,
                toYear = state.selectedYear
            ).getOrNull()

            // Get previous month savings
            val prevMonth = if (state.selectedMonth == 1) 12 else state.selectedMonth - 1
            val prevYear = if (state.selectedMonth == 1) state.selectedYear - 1 else state.selectedYear
            val prevSummary = personalFinanceRepository.getMonthlyFinanceSummary(
                userId = userId,
                month = prevMonth,
                year = prevYear
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    totalIncome = summary.totalIncome,
                    totalExpenses = summary.totalExpenses,
                    netSavings = summary.netSavings,
                    savingsRate = summary.savingsRate,
                    expensesByCategory = summary.expensesByCategory,
                    carryoverAmount = summary.carryoverAmount,
                    hasCarryover = carryover != null,
                    previousMonthSavings = if (prevSummary.netSavings > 0) prevSummary.netSavings else 0.0
                )
            }
        }
    }

    fun setMonth(month: Int) {
        _uiState.update { it.copy(selectedMonth = month) }
        loadData()
    }

    fun setYear(year: Int) {
        _uiState.update { it.copy(selectedYear = year) }
        loadData()
    }

    fun transferCarryover() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            val state = _uiState.value

            val prevMonth = if (state.selectedMonth == 1) 12 else state.selectedMonth - 1
            val prevYear = if (state.selectedMonth == 1) state.selectedYear - 1 else state.selectedYear

            val carryover = MonthlyCarryover(
                id = UUID.randomUUID().toString(),
                userId = userId,
                fromMonth = prevMonth,
                fromYear = prevYear,
                toMonth = state.selectedMonth,
                toYear = state.selectedYear,
                amount = state.previousMonthSavings
            )

            personalFinanceRepository.createMonthlyCarryover(carryover).onSuccess {
                loadData()
            }
        }
    }
}

data class WalletPlannerUiState(
    val isLoading: Boolean = true,
    val currency: String = "USD",
    val selectedMonth: Int = 1,
    val selectedYear: Int = 2025,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val netSavings: Double = 0.0,
    val savingsRate: Double = 0.0,
    val expensesByCategory: Map<String, Double> = emptyMap(),
    val carryoverAmount: Double = 0.0,
    val hasCarryover: Boolean = false,
    val previousMonthSavings: Double = 0.0
)
