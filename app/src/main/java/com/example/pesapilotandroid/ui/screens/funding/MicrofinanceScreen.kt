package com.example.pesapilotandroid.ui.screens.funding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.pesapilotandroid.data.model.Microfinance
import com.example.pesapilotandroid.navigation.NavRoute
import com.example.pesapilotandroid.ui.components.EmptyStateScreen
import com.example.pesapilotandroid.ui.components.LoadingScreen
import com.example.pesapilotandroid.ui.components.PesaPilotTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MicrofinanceScreen(
    navController: NavController,
    viewModel: MicrofinanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = "Microfinance",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingScreen()
            uiState.institutions.isEmpty() -> {
                EmptyStateScreen(
                    title = "No Microfinance Institutions",
                    message = "Check back later for available funding options",
                    icon = Icons.Default.AccountBalance
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
                    items(uiState.institutions) { institution ->
                        MicrofinanceCard(
                            institution = institution,
                            onClick = { 
                                navController.navigate(NavRoute.MicrofinanceDetail(institution.id)) 
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MicrofinanceCard(
    institution: Microfinance,
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
            verticalAlignment = Alignment.Top
        ) {
            // Logo
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(64.dp)
            ) {
                if (institution.logoUrl != null) {
                    AsyncImage(
                        model = institution.logoUrl,
                        contentDescription = institution.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = institution.name,
                    style = MaterialTheme.typography.titleMedium
                )
                
                institution.description?.let { desc ->
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Loan range
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    institution.minLoanAmount?.let { min ->
                        institution.maxLoanAmount?.let { max ->
                            AssistChip(
                                onClick = {},
                                label = { 
                                    Text(
                                        "${formatCurrency(min)} - ${formatCurrency(max)}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.AttachMoney,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                // Interest rate
                institution.minInterestRate?.let { minRate ->
                    institution.maxInterestRate?.let { maxRate ->
                        Text(
                            text = "Interest: ${minRate}% - ${maxRate}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatCurrency(amount: Double): String {
    return when {
        amount >= 1000000 -> String.format("%.1fM", amount / 1000000)
        amount >= 1000 -> String.format("%.0fK", amount / 1000)
        else -> String.format("%.0f", amount)
    }
}
