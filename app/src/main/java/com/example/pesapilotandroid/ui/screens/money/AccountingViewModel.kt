package com.example.pesapilotandroid.ui.screens.money

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.*
import com.example.pesapilotandroid.data.repository.AccountingRepository
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
class AccountingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountingRepository: AccountingRepository,
    private val businessRepository: BusinessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountingUiState())
    val uiState: StateFlow<AccountingUiState> = _uiState.asStateFlow()

    init {
        loadBusinesses()
    }

    private fun loadBusinesses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = authRepository.getCurrentUserId() ?: return@launch

            businessRepository.getUserBusinesses(userId).onSuccess { businesses ->
                _uiState.update { it.copy(businesses = businesses, isLoading = false) }
                if (businesses.isNotEmpty()) {
                    selectBusiness(businesses.first())
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun selectBusiness(business: UserBusiness) {
        _uiState.update { it.copy(selectedBusiness = business) }
        loadAccountingData(business.id)
    }

    private fun loadAccountingData(businessId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = authRepository.getCurrentUserId() ?: return@launch

            // Load accounts
            accountingRepository.getChartOfAccounts(userId, businessId).onSuccess { accounts ->
                _uiState.update { it.copy(accounts = accounts) }
            }

            // Load subcategories
            accountingRepository.getAccountSubcategories(userId, businessId).onSuccess { subs ->
                _uiState.update { it.copy(subcategories = subs) }
            }

            // Load entries
            accountingRepository.getJournalEntriesWithLines(userId, businessId).onSuccess { entries ->
                _uiState.update { it.copy(entries = entries) }
                // Load lines for all entries
                if (entries.isNotEmpty()) {
                    accountingRepository.getLinesForEntries(entries.map { it.id }).onSuccess { lines ->
                        _uiState.update { it.copy(entryLines = lines) }
                    }
                }
            }

            // Load contacts
            accountingRepository.getContacts(userId, businessId).onSuccess { contacts ->
                _uiState.update { it.copy(contacts = contacts) }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun selectTab(tab: AccountingTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun refresh() {
        _uiState.value.selectedBusiness?.let { loadAccountingData(it.id) }
    }

    fun seedDefaults() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            val businessId = _uiState.value.selectedBusiness?.id ?: return@launch

            accountingRepository.seedDefaultSubcategories(userId, businessId)
            accountingRepository.seedDefaultAccounts(userId, businessId)
            loadAccountingData(businessId)
        }
    }
}

enum class AccountingTab(val label: String) {
    TRIAL_BALANCE("Trial Balance"),
    JOURNAL_ENTRIES("Journal Entries"),
    GENERAL_LEDGER("General Ledger"),
    CHART_OF_ACCOUNTS("Chart of Accounts")
}

data class AccountingUiState(
    val isLoading: Boolean = true,
    val businesses: List<UserBusiness> = emptyList(),
    val selectedBusiness: UserBusiness? = null,
    val accounts: List<ChartOfAccount> = emptyList(),
    val subcategories: List<AccountSubcategory> = emptyList(),
    val entries: List<JournalEntry> = emptyList(),
    val entryLines: List<JournalEntryLine> = emptyList(),
    val contacts: List<Contact> = emptyList(),
    val selectedTab: AccountingTab = AccountingTab.JOURNAL_ENTRIES
)
