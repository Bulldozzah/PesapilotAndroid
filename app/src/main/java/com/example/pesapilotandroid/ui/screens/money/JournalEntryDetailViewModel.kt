package com.example.pesapilotandroid.ui.screens.money

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.JournalEntry
import com.example.pesapilotandroid.data.model.JournalEntryLine
import com.example.pesapilotandroid.data.repository.AccountingRepository
import com.example.pesapilotandroid.data.repository.AuthRepository
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
    private val accountingRepository: AccountingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalEntryDetailUiState())
    val uiState: StateFlow<JournalEntryDetailUiState> = _uiState.asStateFlow()

    private var existingEntryId: String? = null

    init {
        _uiState.update { 
            it.copy(
                entryDate = LocalDate.now().toString(),
                debitLines = listOf(EntryLineUi()),
                creditLines = listOf(EntryLineUi())
            )
        }
    }

    fun loadEntry(entryId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            accountingRepository.getJournalEntry(entryId).onSuccess { entry ->
                entry?.let {
                    existingEntryId = it.id
                    _uiState.update { state ->
                        state.copy(
                            referenceNumber = it.reference ?: "",
                            entryDate = it.entryDate,
                            description = it.description ?: "",
                            isPosted = it.isPosted
                        )
                    }

                    // Load lines
                    accountingRepository.getJournalEntryLines(entryId).onSuccess { lines ->
                        val debits = lines.filter { it.debit > 0 }
                            .map { EntryLineUi(it.accountId, it.debit.toString()) }
                        val credits = lines.filter { it.credit > 0 }
                            .map { EntryLineUi(it.accountId, it.credit.toString()) }

                        _uiState.update { state ->
                            state.copy(
                                debitLines = debits.ifEmpty { listOf(EntryLineUi()) },
                                creditLines = credits.ifEmpty { listOf(EntryLineUi()) },
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

    fun addDebitLine() {
        _uiState.update { 
            it.copy(debitLines = it.debitLines + EntryLineUi())
        }
    }

    fun removeDebitLine(index: Int) {
        _uiState.update { state ->
            val newLines = state.debitLines.toMutableList()
            if (newLines.size > 1) {
                newLines.removeAt(index)
            }
            state.copy(debitLines = newLines)
        }
    }

    fun updateDebitLine(index: Int, accountName: String, amount: String) {
        _uiState.update { state ->
            val newLines = state.debitLines.toMutableList()
            newLines[index] = EntryLineUi(accountName, amount)
            state.copy(debitLines = newLines)
        }
    }

    fun addCreditLine() {
        _uiState.update { 
            it.copy(creditLines = it.creditLines + EntryLineUi())
        }
    }

    fun removeCreditLine(index: Int) {
        _uiState.update { state ->
            val newLines = state.creditLines.toMutableList()
            if (newLines.size > 1) {
                newLines.removeAt(index)
            }
            state.copy(creditLines = newLines)
        }
    }

    fun updateCreditLine(index: Int, accountName: String, amount: String) {
        _uiState.update { state ->
            val newLines = state.creditLines.toMutableList()
            newLines[index] = EntryLineUi(accountName, amount)
            state.copy(creditLines = newLines)
        }
    }

    fun saveEntry() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            val state = _uiState.value

            _uiState.update { it.copy(isSaving = true) }

            val entryId = existingEntryId ?: UUID.randomUUID().toString()
            val entry = JournalEntry(
                id = entryId,
                userId = userId,
                entryDate = state.entryDate,
                reference = state.referenceNumber.ifBlank { null },
                description = state.description.ifBlank { null },
                isPosted = state.isPosted
            )

            val result = if (existingEntryId != null) {
                accountingRepository.updateJournalEntry(entry)
            } else {
                accountingRepository.createJournalEntry(entry)
            }

            result
                .onSuccess { savedEntry ->
                    // Create lines
                    val lines = mutableListOf<JournalEntryLine>()
                    
                    state.debitLines.forEach { line ->
                        val amount = line.amount.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            lines.add(JournalEntryLine(
                                id = UUID.randomUUID().toString(),
                                journalEntryId = savedEntry.id,
                                accountId = line.accountName,
                                debit = amount,
                                credit = 0.0
                            ))
                        }
                    }

                    state.creditLines.forEach { line ->
                        val amount = line.amount.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            lines.add(JournalEntryLine(
                                id = UUID.randomUUID().toString(),
                                journalEntryId = savedEntry.id,
                                accountId = line.accountName,
                                debit = 0.0,
                                credit = amount
                            ))
                        }
                    }

                    if (lines.isNotEmpty()) {
                        accountingRepository.createJournalEntryLines(lines)
                    }

                    _uiState.update { it.copy(isSaving = false, isSaved = true) }
                }
                .onFailure { e ->
                    _uiState.update { 
                        it.copy(isSaving = false, error = e.message ?: "Failed to save entry") 
                    }
                }
        }
    }

    fun deleteEntry() {
        viewModelScope.launch {
            existingEntryId?.let { entryId ->
                _uiState.update { it.copy(isSaving = true) }
                
                accountingRepository.deleteJournalEntry(entryId)
                    .onSuccess {
                        _uiState.update { it.copy(isSaving = false, isSaved = true) }
                    }
                    .onFailure { e ->
                        _uiState.update { 
                            it.copy(isSaving = false, error = e.message ?: "Failed to delete entry") 
                        }
                    }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class EntryLineUi(
    val accountName: String = "",
    val amount: String = ""
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
    val debitLines: List<EntryLineUi> = emptyList(),
    val creditLines: List<EntryLineUi> = emptyList()
)
