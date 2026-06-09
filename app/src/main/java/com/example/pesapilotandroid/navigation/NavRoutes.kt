package com.example.pesapilotandroid.navigation

import kotlinx.serialization.Serializable

sealed interface NavRoute {
    // Auth routes
    @Serializable data object Login : NavRoute
    @Serializable data object SignUp : NavRoute
    @Serializable data object ForgotPassword : NavRoute
    @Serializable data object ProfileSetup : NavRoute

    // Main routes
    @Serializable data object Main : NavRoute
    @Serializable data object Dashboard : NavRoute

    // Business routes
    @Serializable data object Businesses : NavRoute
    @Serializable data object BusinessDiscovery : NavRoute
    @Serializable data object MyBusinesses : NavRoute
    @Serializable data object Roadmaps : NavRoute
    @Serializable data object BusinessPlans : NavRoute
    @Serializable data class BusinessDetail(val businessId: String) : NavRoute
    @Serializable data class BusinessPlanDetail(val planId: String?) : NavRoute
    @Serializable data class BusinessTemplateDetail(val templateId: String) : NavRoute

    // Money routes
    @Serializable data object Money : NavRoute
    @Serializable data object IncomeExpenses : NavRoute
    @Serializable data object WalletPlanner : NavRoute
    @Serializable data object SavingsGoals : NavRoute
    @Serializable data object Accounting : NavRoute
    @Serializable data object Reports : NavRoute
    @Serializable data object BankAccounts : NavRoute
    @Serializable data object VendorsCustomers : NavRoute
    @Serializable data class JournalEntryDetail(val entryId: String?) : NavRoute
    
    // Funding routes
    @Serializable data object Funding : NavRoute
    @Serializable data object Microfinance : NavRoute
    @Serializable data object LenderDirectory : NavRoute
    @Serializable data class MicrofinanceDetail(val microfinanceId: String) : NavRoute
    @Serializable data class LoanApplication(val microfinanceId: String) : NavRoute

    // Profile routes
    @Serializable data object Profile : NavRoute
    @Serializable data object EditProfile : NavRoute

    // Admin routes
    @Serializable data object Admin : NavRoute
    @Serializable data object AdminOverview : NavRoute
    @Serializable data object AdminMicrofinance : NavRoute
    @Serializable data class AdminMicrofinanceEdit(val microfinanceId: String?) : NavRoute
}
