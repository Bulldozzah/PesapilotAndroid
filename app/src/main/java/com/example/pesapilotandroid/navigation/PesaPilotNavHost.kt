package com.example.pesapilotandroid.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.pesapilotandroid.ui.screens.admin.AdminMicrofinanceEditScreen
import com.example.pesapilotandroid.ui.screens.admin.AdminMicrofinanceScreen
import com.example.pesapilotandroid.ui.screens.admin.AdminOverviewScreen
import com.example.pesapilotandroid.ui.screens.admin.AdminScreen
import com.example.pesapilotandroid.ui.screens.auth.ForgotPasswordScreen
import com.example.pesapilotandroid.ui.screens.auth.LoginScreen
import com.example.pesapilotandroid.ui.screens.auth.ProfileSetupScreen
import com.example.pesapilotandroid.ui.screens.auth.SignUpScreen
import com.example.pesapilotandroid.ui.screens.businesses.BusinessDetailScreen
import com.example.pesapilotandroid.ui.screens.businesses.BusinessDiscoveryScreen
import com.example.pesapilotandroid.ui.screens.businesses.BusinessPlanDetailScreen
import com.example.pesapilotandroid.ui.screens.businesses.BusinessPlansScreen
import com.example.pesapilotandroid.ui.screens.businesses.BusinessTemplateDetailScreen
import com.example.pesapilotandroid.ui.screens.businesses.BusinessesScreen
import com.example.pesapilotandroid.ui.screens.businesses.MyBusinessesScreen
import com.example.pesapilotandroid.ui.screens.businesses.RoadmapsScreen
import com.example.pesapilotandroid.ui.screens.dashboard.DashboardScreen
import com.example.pesapilotandroid.ui.screens.funding.FundingScreen
import com.example.pesapilotandroid.ui.screens.funding.LenderDirectoryScreen
import com.example.pesapilotandroid.ui.screens.funding.LoanApplicationScreen
import com.example.pesapilotandroid.ui.screens.funding.MicrofinanceDetailScreen
import com.example.pesapilotandroid.ui.screens.funding.MicrofinanceScreen
import com.example.pesapilotandroid.ui.screens.money.AccountingScreen
import com.example.pesapilotandroid.ui.screens.money.BankAccountsScreen
import com.example.pesapilotandroid.ui.screens.money.IncomeExpensesScreen
import com.example.pesapilotandroid.ui.screens.money.JournalEntryDetailScreen
import com.example.pesapilotandroid.ui.screens.money.MoneyScreen
import com.example.pesapilotandroid.ui.screens.money.ReportDetailScreen
import com.example.pesapilotandroid.ui.screens.money.ReportsScreen
import com.example.pesapilotandroid.ui.screens.money.SavingsGoalsScreen
import com.example.pesapilotandroid.ui.screens.money.VendorsCustomersScreen
import com.example.pesapilotandroid.ui.screens.money.WalletPlannerScreen
import com.example.pesapilotandroid.ui.screens.profile.EditProfileScreen
import com.example.pesapilotandroid.ui.screens.profile.ProfileScreen

@Composable
fun PesaPilotNavHost(
    navController: NavHostController,
    startDestination: NavRoute,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth
        composable<NavRoute.Login> {
            LoginScreen(
                onNavigateToSignUp = { navController.navigate(NavRoute.SignUp) },
                onNavigateToForgotPassword = { navController.navigate(NavRoute.ForgotPassword) },
                onLoginSuccess = { isProfileComplete ->
                    if (isProfileComplete) {
                        navController.navigate(NavRoute.Dashboard) {
                            popUpTo(NavRoute.Login) { inclusive = true }
                        }
                    } else {
                        navController.navigate(NavRoute.ProfileSetup) {
                            popUpTo(NavRoute.Login) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable<NavRoute.SignUp> {
            SignUpScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate(NavRoute.ProfileSetup) {
                        popUpTo(NavRoute.Login) { inclusive = true }
                    }
                }
            )
        }

        composable<NavRoute.ForgotPassword> {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<NavRoute.ProfileSetup> {
            ProfileSetupScreen(
                onSetupComplete = {
                    navController.navigate(NavRoute.Dashboard) {
                        popUpTo(NavRoute.ProfileSetup) { inclusive = true }
                    }
                }
            )
        }

        // Dashboard
        composable<NavRoute.Dashboard> {
            DashboardScreen(navController = navController)
        }

        // Businesses
        composable<NavRoute.Businesses> {
            BusinessesScreen(navController = navController)
        }

        composable<NavRoute.BusinessDiscovery> {
            BusinessDiscoveryScreen(navController = navController)
        }

        composable<NavRoute.MyBusinesses> {
            MyBusinessesScreen(navController = navController)
        }

        composable<NavRoute.Roadmaps> {
            RoadmapsScreen(navController = navController)
        }

        composable<NavRoute.BusinessPlans> {
            BusinessPlansScreen(navController = navController)
        }

        composable<NavRoute.BusinessDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.BusinessDetail>()
            BusinessDetailScreen(
                businessId = route.businessId,
                navController = navController
            )
        }

        composable<NavRoute.BusinessPlanDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.BusinessPlanDetail>()
            BusinessPlanDetailScreen(
                planId = route.planId,
                navController = navController
            )
        }

        composable<NavRoute.BusinessTemplateDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.BusinessTemplateDetail>()
            BusinessTemplateDetailScreen(
                templateId = route.templateId,
                navController = navController
            )
        }

        // Money
        composable<NavRoute.Money> {
            MoneyScreen(navController = navController)
        }

        composable<NavRoute.IncomeExpenses> {
            IncomeExpensesScreen(navController = navController)
        }

        composable<NavRoute.WalletPlanner> {
            WalletPlannerScreen(navController = navController)
        }

        composable<NavRoute.SavingsGoals> {
            SavingsGoalsScreen(navController = navController)
        }

        composable<NavRoute.Accounting> {
            AccountingScreen(navController = navController)
        }

        composable<NavRoute.Reports> {
            ReportsScreen(navController = navController)
        }

        composable<NavRoute.BankAccounts> {
            BankAccountsScreen(navController = navController)
        }

        composable<NavRoute.VendorsCustomers> {
            VendorsCustomersScreen(navController = navController)
        }

        composable<NavRoute.JournalEntryDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.JournalEntryDetail>()
            JournalEntryDetailScreen(
                entryId = route.entryId,
                navController = navController
            )
        }

        composable<NavRoute.ReportDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.ReportDetail>()
            ReportDetailScreen(
                reportType = route.reportType,
                navController = navController
            )
        }

        // Funding
        composable<NavRoute.Funding> {
            FundingScreen(navController = navController)
        }

        composable<NavRoute.Microfinance> {
            MicrofinanceScreen(navController = navController)
        }

        composable<NavRoute.LenderDirectory> {
            LenderDirectoryScreen(navController = navController)
        }

        composable<NavRoute.MicrofinanceDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.MicrofinanceDetail>()
            MicrofinanceDetailScreen(
                microfinanceId = route.microfinanceId,
                navController = navController
            )
        }

        composable<NavRoute.LoanApplication> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.LoanApplication>()
            LoanApplicationScreen(
                microfinanceId = route.microfinanceId,
                navController = navController
            )
        }

        // Profile
        composable<NavRoute.Profile> {
            ProfileScreen(
                navController = navController,
                onLogout = {
                    navController.navigate(NavRoute.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<NavRoute.EditProfile> {
            EditProfileScreen(navController = navController)
        }

        // Admin
        composable<NavRoute.Admin> {
            AdminScreen(navController = navController)
        }

        composable<NavRoute.AdminOverview> {
            AdminOverviewScreen(navController = navController)
        }

        composable<NavRoute.AdminMicrofinance> {
            AdminMicrofinanceScreen(navController = navController)
        }

        composable<NavRoute.AdminMicrofinanceEdit> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.AdminMicrofinanceEdit>()
            AdminMicrofinanceEditScreen(
                microfinanceId = route.microfinanceId,
                navController = navController
            )
        }
    }
}
