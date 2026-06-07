package com.example.pesapilotandroid.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.Microfinance
import com.example.pesapilotandroid.data.repository.FundingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AdminMicrofinanceEditViewModel @Inject constructor(
    private val fundingRepository: FundingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminMicrofinanceEditUiState())
    val uiState: StateFlow<AdminMicrofinanceEditUiState> = _uiState.asStateFlow()

    private var existingId: String? = null

    fun loadMicrofinance(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            fundingRepository.getMicrofinanceById(id).onSuccess { mfi ->
                mfi?.let {
                    existingId = it.id
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            name = it.name,
                            description = it.description ?: "",
                            logoUrl = it.logoUrl ?: "",
                            minLoanAmount = it.minLoanAmount?.toString() ?: "",
                            maxLoanAmount = it.maxLoanAmount?.toString() ?: "",
                            minInterestRate = it.minInterestRate?.toString() ?: "",
                            maxInterestRate = it.maxInterestRate?.toString() ?: "",
                            address = it.address ?: "",
                            phoneNumber = it.phoneNumber ?: "",
                            whatsappNumber = it.whatsappNumber ?: "",
                            email = it.email ?: "",
                            website = it.website ?: "",
                            requiredDocuments = it.requiredDocuments.toMutableList()
                        )
                    }
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateName(value: String) = _uiState.update { it.copy(name = value) }
    fun updateDescription(value: String) = _uiState.update { it.copy(description = value) }
    fun updateLogoUrl(value: String) = _uiState.update { it.copy(logoUrl = value) }
    fun updateMinLoanAmount(value: String) = _uiState.update { it.copy(minLoanAmount = value) }
    fun updateMaxLoanAmount(value: String) = _uiState.update { it.copy(maxLoanAmount = value) }
    fun updateMinInterestRate(value: String) = _uiState.update { it.copy(minInterestRate = value) }
    fun updateMaxInterestRate(value: String) = _uiState.update { it.copy(maxInterestRate = value) }
    fun updateAddress(value: String) = _uiState.update { it.copy(address = value) }
    fun updatePhoneNumber(value: String) = _uiState.update { it.copy(phoneNumber = value) }
    fun updateWhatsappNumber(value: String) = _uiState.update { it.copy(whatsappNumber = value) }
    fun updateEmail(value: String) = _uiState.update { it.copy(email = value) }
    fun updateWebsite(value: String) = _uiState.update { it.copy(website = value) }

    fun toggleDocument(doc: String, checked: Boolean) {
        _uiState.update { state ->
            val newDocs = state.requiredDocuments.toMutableList()
            if (checked) {
                if (!newDocs.contains(doc)) newDocs.add(doc)
            } else {
                newDocs.remove(doc)
            }
            state.copy(requiredDocuments = newDocs)
        }
    }

    fun save() {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(isSaving = true) }

            val mfi = Microfinance(
                id = existingId ?: UUID.randomUUID().toString(),
                name = state.name,
                description = state.description.ifBlank { null },
                logoUrl = state.logoUrl.ifBlank { null },
                minLoanAmount = state.minLoanAmount.toDoubleOrNull(),
                maxLoanAmount = state.maxLoanAmount.toDoubleOrNull(),
                minInterestRate = state.minInterestRate.toDoubleOrNull(),
                maxInterestRate = state.maxInterestRate.toDoubleOrNull(),
                address = state.address.ifBlank { null },
                phone = state.phoneNumber.ifBlank { null },
                whatsapp = state.whatsappNumber.ifBlank { null },
                email = state.email.ifBlank { null },
                website = state.website.ifBlank { null },
                requiredDocuments = state.requiredDocuments
            )

            val result = if (existingId != null) {
                fundingRepository.updateMicrofinance(mfi)
            } else {
                fundingRepository.createMicrofinance(mfi)
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, isSaved = true) }
                }
                .onFailure { e ->
                    _uiState.update { 
                        it.copy(isSaving = false, error = e.message ?: "Failed to save") 
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class AdminMicrofinanceEditUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val name: String = "",
    val description: String = "",
    val logoUrl: String = "",
    val minLoanAmount: String = "",
    val maxLoanAmount: String = "",
    val minInterestRate: String = "",
    val maxInterestRate: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val whatsappNumber: String = "",
    val email: String = "",
    val website: String = "",
    val requiredDocuments: MutableList<String> = mutableListOf()
)
