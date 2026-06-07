package com.example.pesapilotandroid.ui.screens.businesses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pesapilotandroid.data.model.BusinessPlan
import com.example.pesapilotandroid.navigation.NavRoute
import com.example.pesapilotandroid.ui.components.EmptyStateScreen
import com.example.pesapilotandroid.ui.components.LoadingScreen
import com.example.pesapilotandroid.ui.components.PesaPilotTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessPlansScreen(
    navController: NavController,
    viewModel: BusinessPlansViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = "Business Plans",
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(NavRoute.BusinessPlanDetail(null)) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Plan")
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingScreen()
            uiState.plans.isEmpty() -> {
                EmptyStateScreen(
                    title = "No Business Plans",
                    message = "Create your first business plan to get started",
                    icon = Icons.Default.Description,
                    actionText = "Create Plan",
                    onAction = { navController.navigate(NavRoute.BusinessPlanDetail(null)) }
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.plans) { plan ->
                        BusinessPlanCard(
                            plan = plan,
                            onClick = { navController.navigate(NavRoute.BusinessPlanDetail(plan.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BusinessPlanCard(
    plan: BusinessPlan,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = plan.title,
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Business Plan",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Show creation date
                plan.createdAt?.let { date ->
                    AssistChip(
                        onClick = {},
                        label = { Text("Created: ${date.take(10)}") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    return when {
        amount >= 1000000 -> String.format("$%.1fM", amount / 1000000)
        amount >= 1000 -> String.format("$%.1fK", amount / 1000)
        else -> String.format("$%.0f", amount)
    }
}
