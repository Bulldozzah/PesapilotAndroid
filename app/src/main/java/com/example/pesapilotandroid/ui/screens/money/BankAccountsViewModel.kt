package com.example.pesapilotandroid.ui.screens.money

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.BankAccount
import com.example.pesapilotandroid.data.repository.AccountingRepository
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
class BankAccountsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountingRepository: AccountingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BankAccountsUiState())
    val uiState: StateFlow<BankAccountsUiState> = _uiState.asStateFlow()

    init {
        loadAccounts()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val userId = authRepository.getCurrentUserId() ?: return@launch

            accountingRepository.getBankAccounts(userId)
                .onSuccess { accounts ->
                    _uiState.update { 
                        it.copy(
                            accounts = accounts,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun createAccount(
        bankName: String,
        accountName: String,
        accountNumber: String,
        currency: String,
        balance: Double
    ) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch

            val account = BankAccount(
                id = UUID.randomUUID().toString(),
                userId = userId,
                bankName = bankName,
                name = accountName,
                accountNumber = accountNumber,
                currency = currency,
                balance = balance
            )

            accountingRepository.createBankAccount(account).onSuccess {
                loadAccounts()
            }
        }
    }

    fun deleteAccount(accountId: String) {
        viewModelScope.launch {
            accountingRepository.deleteBankAccount(accountId).onSuccess {
                loadAccounts()
            }
        }
    }
}

data class BankAccountsUiState(
    val isLoading: Boolean = true,
    val accounts: List<BankAccount> = emptyList()
)
