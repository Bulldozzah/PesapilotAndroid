package com.example.pesapilotandroid.data.repository

import com.example.pesapilotandroid.data.model.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonalFinanceRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    // Personal Income
    suspend fun getPersonalIncome(userId: String, month: Int? = null, year: Int? = null): Result<List<PersonalIncome>> {
        return try {
            val income = supabaseClient.postgrest
                .from("personal_income")
                .select {
                    filter {
                        eq("user_id", userId)
                        if (month != null) eq("month", month)
                        if (year != null) eq("year", year)
                    }
                }
                .decodeList<PersonalIncome>()
            Result.success(income)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPersonalIncome(income: PersonalIncome): Result<PersonalIncome> {
        return try {
            val created = supabaseClient.postgrest
                .from("personal_income")
                .insert(income) {
                    select()
                }
                .decodeSingle<PersonalIncome>()
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePersonalIncome(income: PersonalIncome): Result<PersonalIncome> {
        return try {
            val updated = supabaseClient.postgrest
                .from("personal_income")
                .update(income) {
                    filter {
                        eq("id", income.id)
                    }
                    select()
                }
                .decodeSingle<PersonalIncome>()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePersonalIncome(incomeId: String): Result<Unit> {
        return try {
            supabaseClient.postgrest
                .from("personal_income")
                .delete {
                    filter {
                        eq("id", incomeId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Personal Expenses
    suspend fun getPersonalExpenses(userId: String, month: Int? = null, year: Int? = null): Result<List<PersonalExpense>> {
        return try {
            val expenses = supabaseClient.postgrest
                .from("personal_expenses")
                .select {
                    filter {
                        eq("user_id", userId)
                        // Filter by date range if month/year provided
                        if (month != null && year != null) {
                            val startDate = String.format("%04d-%02d-01", year, month)
                            val endDate = if (month == 12) {
                                String.format("%04d-01-01", year + 1)
                            } else {
                                String.format("%04d-%02d-01", year, month + 1)
                            }
                            gte("expense_date", startDate)
                            lt("expense_date", endDate)
                        }
                    }
                    order("expense_date", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                }
                .decodeList<PersonalExpense>()
            Result.success(expenses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPersonalExpense(expense: PersonalExpense): Result<PersonalExpense> {
        return try {
            val created = supabaseClient.postgrest
                .from("personal_expenses")
                .insert(expense) {
                    select()
                }
                .decodeSingle<PersonalExpense>()
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePersonalExpense(expense: PersonalExpense): Result<PersonalExpense> {
        return try {
            val updated = supabaseClient.postgrest
                .from("personal_expenses")
                .update(expense) {
                    filter {
                        eq("id", expense.id)
                    }
                    select()
                }
                .decodeSingle<PersonalExpense>()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePersonalExpense(expenseId: String): Result<Unit> {
        return try {
            supabaseClient.postgrest
                .from("personal_expenses")
                .delete {
                    filter {
                        eq("id", expenseId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Savings Goals
    suspend fun getSavingsGoals(userId: String): Result<List<SavingsGoal>> {
        return try {
            val goals = supabaseClient.postgrest
                .from("savings_goals")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<SavingsGoal>()
            Result.success(goals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSavingsGoal(goal: SavingsGoal): Result<SavingsGoal> {
        return try {
            val created = supabaseClient.postgrest
                .from("savings_goals")
                .insert(goal) {
                    select()
                }
                .decodeSingle<SavingsGoal>()
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSavingsGoal(goal: SavingsGoal): Result<SavingsGoal> {
        return try {
            val updated = supabaseClient.postgrest
                .from("savings_goals")
                .update(goal) {
                    filter {
                        eq("id", goal.id)
                    }
                    select()
                }
                .decodeSingle<SavingsGoal>()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSavingsGoal(goalId: String): Result<Unit> {
        return try {
            supabaseClient.postgrest
                .from("savings_goals")
                .delete {
                    filter {
                        eq("id", goalId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Monthly Carryover
    suspend fun getMonthlyCarryover(userId: String, toMonth: Int, toYear: Int): Result<MonthlyCarryover?> {
        return try {
            val carryover = supabaseClient.postgrest
                .from("monthly_carryovers")
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("to_month", toMonth)
                        eq("to_year", toYear)
                    }
                }
                .decodeSingleOrNull<MonthlyCarryover>()
            Result.success(carryover)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createMonthlyCarryover(carryover: MonthlyCarryover): Result<MonthlyCarryover> {
        return try {
            val created = supabaseClient.postgrest
                .from("monthly_carryovers")
                .insert(carryover) {
                    select()
                }
                .decodeSingle<MonthlyCarryover>()
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Wallet Planner calculations
    suspend fun getMonthlyFinanceSummary(userId: String, month: Int, year: Int): WalletPlannerSummary {
        val incomeResult = getPersonalIncome(userId, month, year)
        val expenseResult = getPersonalExpenses(userId, month, year)
        val carryoverResult = getMonthlyCarryover(userId, month, year)

        val totalIncome = incomeResult.getOrNull()?.sumOf { it.amount } ?: 0.0
        val totalExpenses = expenseResult.getOrNull()?.sumOf { it.amount } ?: 0.0
        val carryoverAmount = carryoverResult.getOrNull()?.amount ?: 0.0

        val netSavings = totalIncome + carryoverAmount - totalExpenses
        val savingsRate = if (totalIncome + carryoverAmount > 0) {
            (netSavings / (totalIncome + carryoverAmount)) * 100
        } else 0.0

        return WalletPlannerSummary(
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            carryoverAmount = carryoverAmount,
            netSavings = netSavings,
            savingsRate = savingsRate,
            expensesByCategory = expenseResult.getOrNull()
                ?.groupBy { it.category }
                ?.mapValues { entry -> entry.value.sumOf { it.amount } }
                ?: emptyMap()
        )
    }
}

data class WalletPlannerSummary(
    val totalIncome: Double,
    val totalExpenses: Double,
    val carryoverAmount: Double,
    val netSavings: Double,
    val savingsRate: Double,
    val expensesByCategory: Map<String, Double>
)
