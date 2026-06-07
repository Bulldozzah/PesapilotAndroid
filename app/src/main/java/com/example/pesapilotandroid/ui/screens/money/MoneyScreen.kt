package com.example.pesapilotandroid.ui.screens.money

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pesapilotandroid.navigation.NavRoute
import com.example.pesapilotandroid.ui.components.MenuCard
import com.example.pesapilotandroid.ui.components.PesaPilotTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneyScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            PesaPilotTopBar(title = "Money")
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Personal Finance",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                MenuCard(
                    title = "Income & Expenses",
                    description = "Track your personal income and expenses",
                    icon = Icons.Default.SwapVert,
                    onClick = { navController.navigate(NavRoute.IncomeExpenses) }
                )
            }

            item {
                MenuCard(
                    title = "Wallet Planner",
                    description = "Plan your monthly finances",
                    icon = Icons.Default.Wallet,
                    onClick = { navController.navigate(NavRoute.WalletPlanner) }
                )
            }

            item {
                MenuCard(
                    title = "Savings Goals",
                    description = "Set and track savings targets",
                    icon = Icons.Default.Savings,
                    onClick = { navController.navigate(NavRoute.SavingsGoals) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Business Finance",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                MenuCard(
                    title = "Accounting",
                    description = "Manage journal entries and accounts",
                    icon = Icons.Default.Calculate,
                    onClick = { navController.navigate(NavRoute.Accounting) }
                )
            }

            item {
                MenuCard(
                    title = "Reports",
                    description = "View financial reports and analytics",
                    icon = Icons.Default.Assessment,
                    onClick = { navController.navigate(NavRoute.Reports) }
                )
            }

            item {
                MenuCard(
                    title = "Bank Accounts",
                    description = "Manage business bank accounts",
                    icon = Icons.Default.AccountBalance,
                    onClick = { navController.navigate(NavRoute.BankAccounts) }
                )
            }

            item {
                MenuCard(
                    title = "Vendors & Customers",
                    description = "Manage business contacts",
                    icon = Icons.Default.People,
                    onClick = { navController.navigate(NavRoute.VendorsCustomers) }
                )
            }
        }
    }
}
