package com.example.pesapilotandroid.ui.screens.money

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pesapilotandroid.ui.components.LoadingScreen
import com.example.pesapilotandroid.ui.components.PesaPilotButton
import com.example.pesapilotandroid.ui.components.PesaPilotTopBar
import com.example.pesapilotandroid.ui.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletPlannerScreen(
    navController: NavController,
    viewModel: WalletPlannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = "Wallet Planner",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingScreen()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Month/Year Selector
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MonthSelector(
                            selectedMonth = uiState.selectedMonth,
                            onMonthChange = { viewModel.setMonth(it) },
                            modifier = Modifier.weight(1f)
                        )
                        YearSelector(
                            selectedYear = uiState.selectedYear,
                            onYearChange = { viewModel.setYear(it) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Summary Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Income",
                            value = "${uiState.currency} ${formatAmount(uiState.totalIncome)}",
                            icon = Icons.Default.TrendingUp,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Expenses",
                            value = "${uiState.currency} ${formatAmount(uiState.totalExpenses)}",
                            icon = Icons.Default.TrendingDown,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Net Savings",
                            value = "${uiState.currency} ${formatAmount(uiState.netSavings)}",
                            icon = Icons.Default.Savings,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Savings Rate",
                            value = "${String.format("%.1f", uiState.savingsRate)}%",
                            icon = Icons.Default.Percent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Carryover from previous month
                if (uiState.previousMonthSavings > 0 && !uiState.hasCarryover) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Previous Month Savings",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${uiState.currency} ${formatAmount(uiState.previousMonthSavings)}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                PesaPilotButton(
                                    text = "Transfer to This Month",
                                    onClick = { viewModel.transferCarryover() }
                                )
                            }
                        }
                    }
                }

                if (uiState.hasCarryover) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Carryover Applied",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = "${uiState.currency} ${formatAmount(uiState.carryoverAmount)} transferred",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }

                // Expense Breakdown
                if (uiState.expensesByCategory.isNotEmpty()) {
                    item {
                        Text(
                            text = "Expense Breakdown",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                uiState.expensesByCategory.forEach { (category, amount) ->
                                    val percentage = if (uiState.totalExpenses > 0) {
                                        (amount / uiState.totalExpenses * 100)
                                    } else 0.0

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = category.replaceFirstChar { it.uppercase() }
                                                    .replace("_", " "),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            LinearProgressIndicator(
                                                progress = { (percentage / 100).toFloat() },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 4.dp),
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            text = "${String.format("%.0f", percentage)}%",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Budget Status
                item {
                    val budgetStatus = when {
                        uiState.savingsRate >= 20 -> "Excellent" to MaterialTheme.colorScheme.primary
                        uiState.savingsRate >= 10 -> "Good" to MaterialTheme.colorScheme.tertiary
                        uiState.savingsRate >= 0 -> "Fair" to MaterialTheme.colorScheme.secondary
                        else -> "Over Budget" to MaterialTheme.colorScheme.error
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = budgetStatus.second.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when {
                                    uiState.savingsRate >= 10 -> Icons.Default.ThumbUp
                                    uiState.savingsRate >= 0 -> Icons.Default.Info
                                    else -> Icons.Default.Warning
                                },
                                contentDescription = null,
                                tint = budgetStatus.second
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Budget Status: ${budgetStatus.first}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = budgetStatus.second
                                )
                                Text(
                                    text = when {
                                        uiState.savingsRate >= 20 -> "You're saving more than 20% of your income!"
                                        uiState.savingsRate >= 10 -> "You're on track with your savings."
                                        uiState.savingsRate >= 0 -> "Consider reducing expenses to save more."
                                        else -> "Your expenses exceed your income this month."
                                    },
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthSelector(
    selectedMonth: Int,
    onMonthChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = months[selectedMonth - 1],
            onValueChange = {},
            readOnly = true,
            label = { Text("Month") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            months.forEachIndexed { index, month ->
                DropdownMenuItem(
                    text = { Text(month) },
                    onClick = {
                        onMonthChange(index + 1)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YearSelector(
    selectedYear: Int,
    onYearChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val years = (2025..2040).toList()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedYear.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Year") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            years.forEach { year ->
                DropdownMenuItem(
                    text = { Text(year.toString()) },
                    onClick = {
                        onYearChange(year)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun formatAmount(amount: Double): String {
    return String.format("%,.2f", amount)
}
