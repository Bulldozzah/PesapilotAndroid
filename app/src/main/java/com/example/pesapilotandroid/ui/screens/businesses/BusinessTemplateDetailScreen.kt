package com.example.pesapilotandroid.ui.screens.businesses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.pesapilotandroid.ui.components.LoadingScreen
import com.example.pesapilotandroid.ui.components.PesaPilotButton
import com.example.pesapilotandroid.ui.components.PesaPilotTopBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BusinessTemplateDetailScreen(
    templateId: String,
    navController: NavController,
    viewModel: BusinessTemplateDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(templateId) {
        viewModel.loadTemplate(templateId)
    }

    LaunchedEffect(uiState.createdBusinessId) {
        uiState.createdBusinessId?.let { businessId ->
            navController.navigate(NavRoute.BusinessDetail(businessId)) {
                popUpTo(NavRoute.BusinessDiscovery) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = "Business Details",
                onBackClick = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingScreen()
        } else {
            uiState.template?.let { template ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    item {
                        Text(
                            text = template.name,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        template.description?.let { desc ->
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Key Info
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            InfoCard(
                                title = "Difficulty",
                                value = template.difficultyLevel.replaceFirstChar { it.uppercase() },
                                icon = Icons.Default.Speed,
                                modifier = Modifier.weight(1f)
                            )
                            template.estimatedStartupCost?.let { cost ->
                                InfoCard(
                                    title = "Startup Cost",
                                    value = formatCurrency(cost),
                                    icon = Icons.Default.AttachMoney,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    template.estimatedRevenue?.let { revenue ->
                        item {
                            InfoCard(
                                title = "Estimated Monthly Revenue",
                                value = formatCurrency(revenue),
                                icon = Icons.Default.TrendingUp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Overview content
                    template.overviewContent?.let { content ->
                        item {
                            Text(
                                text = "Overview",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = content,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Time to profit
                    item {
                        InfoCard(
                            title = "Time to Profit",
                            value = "${template.timeToProfitMonths} months",
                            icon = Icons.Default.Schedule,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Skills Needed placeholder
                    val skillsNeeded = listOf<String>() // Skills now in separate table
                    if (skillsNeeded.isNotEmpty()) {
                        item {
                            Text(
                                text = "Skills Needed",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        item {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                skillsNeeded.forEach { skill ->
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(skill) }
                                    )
                                }
                            }
                        }
                    }

                    // Tools Required - placeholder (tools now in separate table)
                    // Removed as toolsRequired no longer exists in BusinessTemplate

                    // Roadmap Steps
                    if (uiState.steps.isNotEmpty()) {
                        item {
                            Text(
                                text = "Setup Roadmap",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        items(uiState.steps) { step ->
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Surface(
                                        shape = MaterialTheme.shapes.small,
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = step.stepNumber.toString(),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = step.title,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        step.description?.let { desc ->
                                            Text(
                                                text = desc,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Start Business Button
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        PesaPilotButton(
                            text = "Start This Business",
                            onClick = { viewModel.startBusiness() },
                            isLoading = uiState.isCreating,
                            icon = Icons.Default.RocketLaunch
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium
                )
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
