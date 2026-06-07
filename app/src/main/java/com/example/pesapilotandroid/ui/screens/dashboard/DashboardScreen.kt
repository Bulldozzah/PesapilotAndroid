package com.example.pesapilotandroid.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pesapilotandroid.navigation.NavRoute
import com.example.pesapilotandroid.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Welcome back,",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = uiState.userName.ifEmpty { "User" },
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
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
                // Financial Overview
                item {
                    Text(
                        text = "Financial Overview",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            StatCard(
                                title = "Net Savings",
                                value = "${uiState.currency} ${formatAmount(uiState.netSavings)}",
                                icon = Icons.Default.Savings,
                                modifier = Modifier.width(160.dp)
                            )
                        }
                        item {
                            StatCard(
                                title = "This Month",
                                value = "${uiState.currency} ${formatAmount(uiState.monthlyIncome)}",
                                subtitle = "Income",
                                icon = Icons.Default.TrendingUp,
                                modifier = Modifier.width(160.dp)
                            )
                        }
                        item {
                            StatCard(
                                title = "Expenses",
                                value = "${uiState.currency} ${formatAmount(uiState.monthlyExpenses)}",
                                icon = Icons.Default.TrendingDown,
                                modifier = Modifier.width(160.dp)
                            )
                        }
                    }
                }

                // Quick Actions
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionCard(
                            title = "Add Income",
                            icon = Icons.Default.Add,
                            onClick = { navController.navigate(NavRoute.IncomeExpenses) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionCard(
                            title = "Add Expense",
                            icon = Icons.Default.Remove,
                            onClick = { navController.navigate(NavRoute.IncomeExpenses) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionCard(
                            title = "New Business",
                            icon = Icons.Default.Business,
                            onClick = { navController.navigate(NavRoute.BusinessDiscovery) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Active Businesses
                if (uiState.activeBusinesses.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "My Businesses",
                                style = MaterialTheme.typography.titleMedium
                            )
                            TextButton(onClick = { navController.navigate(NavRoute.MyBusinesses) }) {
                                Text("See All")
                            }
                        }
                    }

                    items(uiState.activeBusinesses.take(3)) { business ->
                        val status = if (business.startedAt != null) "active" else "planning"
                        BusinessCard(
                            name = business.name,
                            status = status,
                            progress = 1, // Progress now tracked via step_progress table
                            onClick = { navController.navigate(NavRoute.BusinessDetail(business.id)) }
                        )
                    }
                }

                // Savings Goals
                if (uiState.savingsGoals.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Savings Goals",
                                style = MaterialTheme.typography.titleMedium
                            )
                            TextButton(onClick = { navController.navigate(NavRoute.SavingsGoals) }) {
                                Text("See All")
                            }
                        }
                    }

                    items(uiState.savingsGoals.take(2)) { goal ->
                        SavingsGoalCard(
                            name = goal.goalName,
                            current = goal.currentAmount,
                            target = goal.targetAmount,
                            currency = uiState.currency
                        )
                    }
                }

                // Explore Section
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Explore",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                item {
                    MenuCard(
                        title = "Discover Business Ideas",
                        description = "Find the perfect business for you",
                        icon = Icons.Default.Lightbulb,
                        onClick = { navController.navigate(NavRoute.BusinessDiscovery) }
                    )
                }

                item {
                    MenuCard(
                        title = "Find Funding",
                        description = "Connect with lenders and microfinance",
                        icon = Icons.Default.AccountBalance,
                        onClick = { navController.navigate(NavRoute.Funding) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun BusinessCard(
    name: String,
    status: String,
    progress: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Step $progress • ${status.replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SavingsGoalCard(
    name: String,
    current: Double,
    target: Double,
    currency: String
) {
    val progress = if (target > 0) (current / target).coerceIn(0.0, 1.0) else 0.0

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress.toFloat() },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$currency ${formatAmount(current)} / $currency ${formatAmount(target)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatAmount(amount: Double): String {
    return String.format("%,.2f", amount)
}
