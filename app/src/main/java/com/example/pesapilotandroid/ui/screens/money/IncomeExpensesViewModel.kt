package com.example.pesapilotandroid.ui.screens.money

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.PersonalExpense
import com.example.pesapilotandroid.data.model.PersonalIncome
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
class IncomeExpensesViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val personalFinanceRepository: PersonalFinanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncomeExpensesUiState())
    val uiState: StateFlow<IncomeExpensesUiState> = _uiState.asStateFlow()

    private val currentDate = LocalDate.now()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val userId = authRepository.getCurrentUserId() ?: return@launch

            // Get currency from profile
            authRepository.getUserProfile(userId).onSuccess { profile ->
                _uiState.update { it.copy(currency = profile?.currency ?: "USD") }
            }

            // Load income
            personalFinanceRepository.getPersonalIncome(
                userId = userId,
                month = currentDate.monthValue,
                year = currentDate.year
            ).onSuccess { incomes ->
                _uiState.update { it.copy(incomes = incomes) }
            }

            // Load expenses
            personalFinanceRepository.getPersonalExpenses(
                userId = userId,
                month = currentDate.monthValue,
                year = currentDate.year
            ).onSuccess { expenses ->
                _uiState.update { it.copy(expenses = expenses, isLoading = false) }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun addIncome(source: String, amount: Double, frequency: String) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch

            val income = PersonalIncome(
                id = UUID.randomUUID().toString(),
                userId = userId,
                source = source,
                amount = amount,
                frequency = frequency,
                month = currentDate.monthValue,
                year = currentDate.year
            )

            personalFinanceRepository.createPersonalIncome(income).onSuccess {
                loadData()
            }
        }
    }

    fun addExpense(category: String, amount: Double, description: String?) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch

            val expense = PersonalExpense(
                id = UUID.randomUUID().toString(),
                userId = userId,
                category = category,
                amount = amount,
                expenseDate = currentDate.toString(),
                description = description
            )

            personalFinanceRepository.createPersonalExpense(expense).onSuccess {
                loadData()
            }
        }
    }

    fun deleteIncome(incomeId: String) {
        viewModelScope.launch {
            personalFinanceRepository.deletePersonalIncome(incomeId).onSuccess {
                loadData()
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            personalFinanceRepository.deletePersonalExpense(expenseId).onSuccess {
                loadData()
            }
        }
    }
}

data class IncomeExpensesUiState(
    val isLoading: Boolean = true,
    val currency: String = "USD",
    val incomes: List<PersonalIncome> = emptyList(),
    val expenses: List<PersonalExpense> = emptyList()
)
