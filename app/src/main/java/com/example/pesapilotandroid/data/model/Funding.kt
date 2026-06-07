package com.example.pesapilotandroid.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Matches: public.microfinance_institutions table
@Serializable
data class Microfinance(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    @SerialName("logo_url")
    val logoUrl: String? = null,
    val address: String? = null,
    val country: String = "Zambia",
    val phone: String? = null,
    val whatsapp: String? = null,
    val email: String? = null,
    val website: String? = null,
    @SerialName("min_loan")
    val minLoanAmount: Double? = null,
    @SerialName("max_loan")
    val maxLoanAmount: Double? = null,
    @SerialName("interest_rate_min")
    val minInterestRate: Double? = null,
    @SerialName("interest_rate_max")
    val maxInterestRate: Double? = null,
    @SerialName("required_documents")
    val requiredDocuments: List<String> = emptyList(),
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("created_by")
    val createdBy: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
) {
    // Helper for backward compatibility
    val phoneNumber: String? get() = phone
    val whatsappNumber: String? get() = whatsapp
}

// Matches: public.lenders table
@Serializable
data class Lender(
    val id: String = "",
    val name: String = "",
    val country: String = "Kenya",
    val type: String = "bank",
    val description: String? = null,
    @SerialName("min_loan")
    val minLoanAmount: Double? = null,
    @SerialName("max_loan")
    val maxLoanAmount: Double? = null,
    @SerialName("interest_rate_min")
    val minInterestRate: Double? = null,
    @SerialName("interest_rate_max")
    val maxInterestRate: Double? = null,
    val requirements: String? = null,
    val website: String? = null,
    @SerialName("logo_url")
    val logoUrl: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
) {
    // Helper for backward compatibility
    val lenderType: String get() = type
}

@Serializable
data class LoanApplication(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("microfinance_id")
    val microfinanceId: String = "",
    val status: String = "pending",
    @SerialName("document_urls")
    val documentUrls: List<String> = emptyList(),
    @SerialName("submitted_at")
    val submittedAt: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

// Matches: public.lender_type enum
enum class LenderType(val value: String, val displayName: String) {
    BANK("bank", "Bank"),
    MICROFINANCE("microfinance", "Microfinance"),
    SACCO("sacco", "SACCO"),
    DIGITAL("digital", "Digital Lender"),
    GOVERNMENT("government", "Government")
}

// Required documents as stored in microfinance_institutions.required_documents array
enum class RequiredDocument(val value: String, val displayName: String) {
    NRC("NRC", "NRC / National ID"),
    PROOF_OF_RESIDENCE("Proof of Residence", "Proof of Residence"),
    SALARY_SLIP("Salary Slip", "Salary Slip"),
    BANK_STATEMENT("Bank Statement", "Bank Statement"),
    PASSPORT_PHOTO("Passport Photo", "Passport Photo"),
    EMPLOYMENT_LETTER("Employment Letter", "Employment Letter"),
    BUSINESS_REGISTRATION("Business Registration", "Business Registration"),
    COLLATERAL_DOCUMENTS("Collateral Documents", "Collateral Documents")
}

enum class ApplicationStatus(val value: String) {
    PENDING("pending"),
    SUBMITTED("submitted"),
    UNDER_REVIEW("under_review"),
    APPROVED("approved"),
    REJECTED("rejected")
}
