package com.example.pesapilotandroid.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Matches: public.chart_of_accounts table
@Serializable
data class ChartOfAccount(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("user_business_id")
    val userBusinessId: String? = null,
    val code: String = "",
    val name: String = "",
    val type: String = "asset",
    @SerialName("is_personal")
    val isPersonal: Boolean = false,
    @SerialName("is_active")
    val isActive: Boolean = true,
    val subcategory: String? = null,
    @SerialName("account_type")
    val accountType: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
) {
    // Helper for backward compatibility
    val accountCode: String get() = code
    val accountName: String get() = name
}

// Matches: public.journal_entries table
@Serializable
data class JournalEntry(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("user_business_id")
    val userBusinessId: String? = null,
    @SerialName("entry_date")
    val entryDate: String = "",
    val reference: String? = null,
    val description: String? = null,
    @SerialName("is_posted")
    val isPosted: Boolean = true,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
) {
    // Helper for backward compatibility
    val referenceNumber: String get() = reference ?: ""
}

// Matches: public.journal_lines table
@Serializable
data class JournalEntryLine(
    val id: String = "",
    @SerialName("journal_entry_id")
    val journalEntryId: String = "",
    @SerialName("account_id")
    val accountId: String = "",
    val debit: Double = 0.0,
    val credit: Double = 0.0,
    val description: String? = null,
    @SerialName("vendor_id")
    val vendorId: String? = null,
    @SerialName("customer_id")
    val customerId: String? = null,
    @SerialName("transaction_type")
    val transactionType: String? = null,
    @SerialName("tax_amount")
    val taxAmount: Double = 0.0,
    val memo: String? = null
) {
    // Helper for backward compatibility
    val debitAmount: Double get() = debit
    val creditAmount: Double get() = credit
}

// Matches: public.bank_accounts table
@Serializable
data class BankAccount(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("user_business_id")
    val userBusinessId: String? = null,
    val name: String = "",
    @SerialName("bank_name")
    val bankName: String? = null,
    @SerialName("account_number")
    val accountNumber: String? = null,
    val type: String = "checking",
    val currency: String = "KES",
    val balance: Double = 0.0,
    @SerialName("is_active")
    val isActive: Boolean = true,
    val notes: String? = null,
    @SerialName("account_code")
    val accountCode: String? = null,
    @SerialName("chart_account_id")
    val chartAccountId: String? = null,
    @SerialName("current_balance")
    val currentBalance: Double = 0.0,
    @SerialName("created_at")
    val createdAt: String? = null
) {
    // Helper for backward compatibility
    val accountName: String get() = name
}

// Matches: public.contacts table
@Serializable
data class Contact(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("user_business_id")
    val userBusinessId: String? = null,
    val type: String = "customer",
    val name: String = "",
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val notes: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
) {
    // Helper for backward compatibility
    val contactType: String get() = type
}

// Matches: public.vendors table
@Serializable
data class Vendor(
    val id: String = "",
    @SerialName("user_business_id")
    val userBusinessId: String = "",
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("vendor_name")
    val vendorName: String = "",
    val email: String? = null,
    val phone: String? = null,
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("created_at")
    val createdAt: String? = null
)

// Matches: public.customers table
@Serializable
data class Customer(
    val id: String = "",
    @SerialName("user_business_id")
    val userBusinessId: String = "",
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("customer_name")
    val customerName: String = "",
    val email: String? = null,
    val phone: String? = null,
    @SerialName("is_active")
    val isActive: Boolean = true,
    @SerialName("created_at")
    val createdAt: String? = null
)

// Matches: public.account_subcategories table
@Serializable
data class AccountSubcategory(
    val id: String = "",
    @SerialName("user_business_id")
    val userBusinessId: String = "",
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("account_type")
    val accountType: String = "",
    val name: String = "",
    @SerialName("display_order")
    val displayOrder: Int = 0,
    @SerialName("is_system")
    val isSystem: Boolean = false,
    @SerialName("created_at")
    val createdAt: String? = null
)

// Matches: public.account_type enum
enum class AccountType(val value: String, val displayName: String) {
    ASSET("asset", "Asset"),
    LIABILITY("liability", "Liability"),
    EQUITY("equity", "Equity"),
    INCOME("income", "Income"),
    EXPENSE("expense", "Expense"),
    COGS("cogs", "Cost of Goods Sold"),
    OTHER_INCOME("other_income", "Other Income"),
    OTHER_EXPENSE("other_expense", "Other Expense")
}

// Matches: public.contact_type enum
enum class ContactType(val value: String) {
    CUSTOMER("customer"),
    VENDOR("vendor")
}

// Matches: public.bank_account_type enum
enum class BankAccountType(val value: String, val displayName: String) {
    CHECKING("checking", "Checking"),
    SAVINGS("savings", "Savings"),
    MOBILE_MONEY("mobile_money", "Mobile Money"),
    CASH("cash", "Cash")
}
