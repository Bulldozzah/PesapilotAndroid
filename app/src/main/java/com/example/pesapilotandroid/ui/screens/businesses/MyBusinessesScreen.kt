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
import com.example.pesapilotandroid.data.model.UserBusiness
import com.example.pesapilotandroid.navigation.NavRoute
import com.example.pesapilotandroid.ui.components.EmptyStateScreen
import com.example.pesapilotandroid.ui.components.LoadingScreen
import com.example.pesapilotandroid.ui.components.PesaPilotTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBusinessesScreen(
    navController: NavController,
    viewModel: MyBusinessesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = "My Businesses",
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(NavRoute.BusinessDiscovery) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Business")
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingScreen()
            uiState.businesses.isEmpty() -> {
                EmptyStateScreen(
                    title = "No Businesses Yet",
                    message = "Start your entrepreneurial journey by discovering business ideas",
                    icon = Icons.Default.Business,
                    actionText = "Discover Ideas",
                    onAction = { navController.navigate(NavRoute.BusinessDiscovery) }
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
                    items(uiState.businesses) { business ->
                        UserBusinessCard(
                            business = business,
                            onClick = { navController.navigate(NavRoute.BusinessDetail(business.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserBusinessCard(
    business: UserBusiness,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = business.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    business.description?.let { desc ->
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // Show started status based on startedAt
                val status = if (business.startedAt != null) "active" else "planning"
                StatusChip(status = status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Budget info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Budget: ${business.currency} ${business.budget}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (color, text) = when (status.lowercase()) {
        "idea" -> MaterialTheme.colorScheme.secondary to "Idea"
        "planning" -> MaterialTheme.colorScheme.tertiary to "Planning"
        "setup" -> MaterialTheme.colorScheme.primary to "Setup"
        "launched" -> MaterialTheme.colorScheme.primary to "Launched"
        "growing" -> MaterialTheme.colorScheme.primary to "Growing"
        else -> MaterialTheme.colorScheme.secondary to status
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
