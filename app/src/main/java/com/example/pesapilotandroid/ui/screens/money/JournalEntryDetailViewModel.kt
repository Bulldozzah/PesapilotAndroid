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
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class JournalEntryDetailViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountingRepository: AccountingRepository,
    private val businessRepository: BusinessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalEntryDetailUiState())
    val uiState: StateFlow<JournalEntryDetailUiState> = _uiState.asStateFlow()

    private var existingEntryId: String? = null
    private var businessId: String? = null

    init {
        _uiState.update {
            it.copy(
                entryDate = LocalDate.now().toString(),
                lines = listOf(EntryLineUi(), EntryLineUi())
            )
        }
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch

            businessRepository.getUserBusinesses(userId).onSuccess { businesses ->
                _uiState.update { it.copy(businesses = businesses) }
                if (businesses.isNotEmpty()) {
                    selectBusiness(businesses.first())
                }
            }
        }
    }

    private fun selectBusiness(business: UserBusiness) {
        businessId = business.id
        _uiState.update { it.copy(selectedBusiness = business) }
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            accountingRepository.getChartOfAccounts(userId, business.id).onSuccess { accounts ->
                _uiState.update { it.copy(accounts = accounts) }
            }
            accountingRepository.getContacts(userId, business.id).onSuccess { contacts ->
                _uiState.update { it.copy(contacts = contacts) }
            }
        }
    }

    fun onBusinessSelected(business: UserBusiness) {
        selectBusiness(business)
    }

    fun loadEntry(entryId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            accountingRepository.getJournalEntry(entryId).onSuccess { entry ->
                entry?.let {
                    existingEntryId = it.id
                    businessId = it.userBusinessId
                    _uiState.update { state ->
                        state.copy(
                            referenceNumber = it.reference ?: "",
                            entryDate = it.entryDate,
                            description = it.description ?: "",
                            isPosted = it.isPosted
                        )
                    }
                    // Load accounts for this business
                    val userId = authRepository.getCurrentUserId() ?: return@launch
                    it.userBusinessId?.let { bid ->
                        accountingRepository.getChartOfAccounts(userId, bid).onSuccess { accounts ->
                            _uiState.update { s -> s.copy(accounts = accounts) }
                        }
                        accountingRepository.getContacts(userId, bid).onSuccess { contacts ->
                            _uiState.update { s -> s.copy(contacts = contacts) }
                        }
                    }
                    // Load lines
                    accountingRepository.getJournalEntryLines(entryId).onSuccess { lines ->
                        val uiLines = lines.map { l ->
                            EntryLineUi(
                                accountId = l.accountId,
                                debit = if (l.debit > 0) l.debit.toString() else "",
                                credit = if (l.credit > 0) l.credit.toString() else "",
                                memo = l.memo ?: "",
                                vendorId = l.vendorId,
                                customerId = l.customerId,
                                transactionType = l.transactionType,
                                taxAmount = if (l.taxAmount > 0) l.taxAmount.toString() else ""
                            )
                        }
                        _uiState.update { s ->
                            s.copy(
                                lines = uiLines.ifEmpty { listOf(EntryLineUi(), EntryLineUi()) },
                                isLoading = false
                            )
                        }
                    }
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateReferenceNumber(value: String) = _uiState.update { it.copy(referenceNumber = value) }
    fun updateEntryDate(value: String) = _uiState.update { it.copy(entryDate = value) }
    fun updateDescription(value: String) = _uiState.update { it.copy(description = value) }

    fun addLine() {
        _uiState.update { it.copy(lines = it.lines + EntryLineUi()) }
    }

    fun removeLine(index: Int) {
        _uiState.update { state ->
            val newLines = state.lines.toMutableList()
            if (newLines.size > 2) newLines.removeAt(index)
            state.copy(lines = newLines)
        }
    }

    fun updateLineAccount(index: Int, accountId: String) {
        _uiState.update { state ->
            val newLines = state.lines.toMutableList()
            newLines[index] = newLines[index].copy(accountId = accountId)
            state.copy(lines = newLines)
        }
    }

    fun updateLineDebit(index: Int, value: String) {
        _uiState.update { state ->
            val newLines = state.lines.toMutableList()
            newLines[index] = newLines[index].copy(debit = value, credit = "")
            state.copy(lines = newLines)
        }
    }

    fun updateLineCredit(index: Int, value: String) {
        _uiState.update { state ->
            val newLines = state.lines.toMutableList()
            newLines[index] = newLines[index].copy(credit = value, debit = "")
            state.copy(lines = newLines)
        }
    }

    fun updateLineMemo(index: Int, value: String) {
        _uiState.update { state ->
            val newLines = state.lines.toMutableList()
            newLines[index] = newLines[index].copy(memo = value)
            state.copy(lines = newLines)
        }
    }

    fun updateLineVendor(index: Int, vendorId: String?) {
        _uiState.update { state ->
            val newLines = state.lines.toMutableList()
            newLines[index] = newLines[index].copy(vendorId = vendorId)
            state.copy(lines = newLines)
        }
    }

    fun updateLineCustomer(index: Int, customerId: String?) {
        _uiState.update { state ->
            val newLines = state.lines.toMutableList()
            newLines[index] = newLines[index].copy(customerId = customerId)
            state.copy(lines = newLines)
        }
    }

    fun updateLineTax(index: Int, value: String) {
        _uiState.update { state ->
            val newLines = state.lines.toMutableList()
            newLines[index] = newLines[index].copy(taxAmount = value)
            state.copy(lines = newLines)
        }
    }

    fun saveEntry(post: Boolean) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            val state = _uiState.value
            val bid = businessId ?: state.selectedBusiness?.id ?: return@launch

            _uiState.update { it.copy(isSaving = true) }

            val entryId = existingEntryId ?: UUID.randomUUID().toString()
            val ref = state.referenceNumber.ifBlank {
                // Auto-generate reference
                val year = LocalDate.now().year
                val count = state.entries.size + 1
                "JE-$year-${count.toString().padStart(4, '0')}"
            }

            val entry = JournalEntry(
                id = entryId, userId = userId, userBusinessId = bid,
                entryDate = state.entryDate, reference = ref,
                description = state.description.ifBlank { "Journal Entry" },
                isPosted = post
            )

            val result = if (existingEntryId != null) {
                accountingRepository.updateJournalEntry(entry)
            } else {
                accountingRepository.createJournalEntry(entry)
            }

            result.onSuccess { savedEntry ->
                // Delete old lines if updating
                if (existingEntryId != null) {
                    accountingRepository.deleteJournalEntryLines(savedEntry.id)
                }

                // Create new lines
                val lines = state.lines.mapNotNull { line ->
                    val debit = line.debit.toDoubleOrNull() ?: 0.0
                    val credit = line.credit.toDoubleOrNull() ?: 0.0
                    if (debit == 0.0 && credit == 0.0) return@mapNotNull null
                    JournalEntryLine(
                        id = UUID.randomUUID().toString(),
                        journalEntryId = savedEntry.id,
                        accountId = line.accountId,
                        debit = debit, credit = credit,
                        memo = line.memo.ifBlank { null },
                        vendorId = line.vendorId,
                        customerId = line.customerId,
                        transactionType = line.transactionType,
                        taxAmount = line.taxAmount.toDoubleOrNull() ?: 0.0
                    )
                }
                if (lines.isNotEmpty()) {
                    accountingRepository.createJournalEntryLines(lines)
                }
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false, error = e.message ?: "Failed to save") }
            }
        }
    }

    fun deleteEntry() {
        viewModelScope.launch {
            existingEntryId?.let { entryId ->
                _uiState.update { it.copy(isSaving = true) }
                accountingRepository.deleteJournalEntry(entryId)
                    .onSuccess { _uiState.update { it.copy(isSaving = false, isSaved = true) } }
                    .onFailure { e -> _uiState.update { it.copy(isSaving = false, error = e.message) } }
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}

data class EntryLineUi(
    val accountId: String = "",
    val debit: String = "",
    val credit: String = "",
    val memo: String = "",
    val vendorId: String? = null,
    val customerId: String? = null,
    val transactionType: String? = null,
    val taxAmount: String = ""
)

data class JournalEntryDetailUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val referenceNumber: String = "",
    val entryDate: String = "",
    val description: String = "",
    val isPosted: Boolean = false,
    val lines: List<EntryLineUi> = emptyList(),
    val accounts: List<ChartOfAccount> = emptyList(),
    val businesses: List<UserBusiness> = emptyList(),
    val selectedBusiness: UserBusiness? = null,
    val contacts: List<Contact> = emptyList(),
    val entries: List<JournalEntry> = emptyList()
)
