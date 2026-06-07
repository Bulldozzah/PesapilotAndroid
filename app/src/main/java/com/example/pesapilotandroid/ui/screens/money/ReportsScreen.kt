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
fun ReportsScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = "Financial Reports",
                onBackClick = { navController.popBackStack() }
            )
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
                MenuCard(
                    title = "Income Statement",
                    description = "View profit and loss over a period",
                    icon = Icons.Default.TrendingUp,
                    onClick = { navController.navigate(NavRoute.ReportDetail("income_statement")) }
                )
            }

            item {
                MenuCard(
                    title = "Balance Sheet",
                    description = "View assets, liabilities, and equity",
                    icon = Icons.Default.Balance,
                    onClick = { navController.navigate(NavRoute.ReportDetail("balance_sheet")) }
                )
            }

            item {
                MenuCard(
                    title = "Cash Flow Statement",
                    description = "Track cash movement",
                    icon = Icons.Default.Payments,
                    onClick = { navController.navigate(NavRoute.ReportDetail("cash_flow")) }
                )
            }

            item {
                MenuCard(
                    title = "Trial Balance",
                    description = "View account balances",
                    icon = Icons.Default.AccountBalance,
                    onClick = { navController.navigate(NavRoute.ReportDetail("trial_balance")) }
                )
            }

            item {
                MenuCard(
                    title = "General Ledger",
                    description = "Detailed transaction history",
                    icon = Icons.Default.MenuBook,
                    onClick = { navController.navigate(NavRoute.ReportDetail("general_ledger")) }
                )
            }

            item {
                MenuCard(
                    title = "Expense Analysis",
                    description = "Expense breakdown and trends",
                    icon = Icons.Default.PieChart,
                    onClick = { navController.navigate(NavRoute.ReportDetail("expense_analysis")) }
                )
            }

            item {
                MenuCard(
                    title = "Revenue Analysis",
                    description = "Revenue sources and trends",
                    icon = Icons.Default.ShowChart,
                    onClick = { navController.navigate(NavRoute.ReportDetail("revenue_analysis")) }
                )
            }
        }
    }
}
