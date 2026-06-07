package com.example.pesapilotandroid.ui.screens.money

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.Contact
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
class VendorsCustomersViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountingRepository: AccountingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VendorsCustomersUiState())
    val uiState: StateFlow<VendorsCustomersUiState> = _uiState.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val userId = authRepository.getCurrentUserId() ?: return@launch

            accountingRepository.getContacts(userId)
                .onSuccess { contacts ->
                    _uiState.update { 
                        it.copy(
                            contacts = contacts,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun createContact(
        name: String,
        contactType: String,
        phone: String?,
        email: String?,
        address: String?
    ) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch

            val contact = Contact(
                id = UUID.randomUUID().toString(),
                userId = userId,
                name = name,
                type = contactType,
                phone = phone,
                email = email,
                address = address
            )

            accountingRepository.createContact(contact).onSuccess {
                loadContacts()
            }
        }
    }

    fun deleteContact(contactId: String) {
        viewModelScope.launch {
            accountingRepository.deleteContact(contactId).onSuccess {
                loadContacts()
            }
        }
    }
}

data class VendorsCustomersUiState(
    val isLoading: Boolean = true,
    val contacts: List<Contact> = emptyList()
)
