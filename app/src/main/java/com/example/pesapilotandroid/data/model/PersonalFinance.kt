package com.example.pesapilotandroid.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Matches: public.personal_income table
@Serializable
data class PersonalIncome(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    val source: String = "",
    val amount: Double = 0.0,
    val frequency: String = "Monthly",
    val month: Int = 1,
    val year: Int = 2025,
    @SerialName("created_at")
    val createdAt: String? = null
)

// Matches: public.personal_expenses table
@Serializable
data class PersonalExpense(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    val category: String = "",
    val amount: Double = 0.0,
    @SerialName("expense_date")
    val expenseDate: String = "",
    val description: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

// Matches: public.savings_goals table
@Serializable
data class SavingsGoal(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    val name: String = "",
    @SerialName("target_amount")
    val targetAmount: Double = 0.0,
    @SerialName("current_amount")
    val currentAmount: Double = 0.0,
    @SerialName("target_date")
    val targetDate: String? = null,
    val month: Int? = null,
    val year: Int? = null,
    val deadline: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
) {
    // Helper for backward compatibility
    val goalName: String get() = name
}

// Matches: public.personal_budgets table
@Serializable
data class PersonalBudget(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    val category: String = "",
    @SerialName("limit_amount")
    val limitAmount: Double = 0.0,
    val month: Int = 1,
    val year: Int = 2025,
    @SerialName("created_at")
    val createdAt: String? = null
)

// Matches: public.expense_categories table
@Serializable
data class ExpenseCategoryRecord(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    val name: String = "",
    @SerialName("is_default")
    val isDefault: Boolean = false,
    @SerialName("created_at")
    val createdAt: String? = null
)

// Note: MonthlyCarryover is not in the SQL schema, keeping for app logic
@Serializable
data class MonthlyCarryover(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("from_month")
    val fromMonth: Int = 1,
    @SerialName("from_year")
    val fromYear: Int = 2025,
    @SerialName("to_month")
    val toMonth: Int = 2,
    @SerialName("to_year")
    val toYear: Int = 2025,
    val amount: Double = 0.0,
    @SerialName("created_at")
    val createdAt: String? = null
)

// Matches: frequency CHECK constraint in personal_income
enum class IncomeFrequency(val value: String, val displayName: String) {
    MONTHLY("Monthly", "Monthly"),
    WEEKLY("Weekly", "Weekly"),
    YEARLY("Yearly", "Yearly"),
    ONE_TIME("One-time", "One-time")
}

enum class ExpenseCategory(val value: String, val displayName: String) {
    HOUSING("housing", "Housing"),
    TRANSPORTATION("transportation", "Transportation"),
    FOOD("food", "Food"),
    UTILITIES("utilities", "Utilities"),
    HEALTHCARE("healthcare", "Healthcare"),
    INSURANCE("insurance", "Insurance"),
    DEBT_PAYMENTS("debt_payments", "Debt Payments"),
    ENTERTAINMENT("entertainment", "Entertainment"),
    CLOTHING("clothing", "Clothing"),
    PERSONAL_CARE("personal_care", "Personal Care"),
    EDUCATION("education", "Education"),
    GIFTS("gifts", "Gifts"),
    SAVINGS("savings", "Savings"),
    INVESTMENTS("investments", "Investments"),
    CHILDCARE("childcare", "Childcare"),
    PET_CARE("pet_care", "Pet Care"),
    TRAVEL("travel", "Travel"),
    OTHER("other", "Other")
}
