package com.example.pesapilotandroid.ui.screens.funding

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.pesapilotandroid.data.model.Lender
import com.example.pesapilotandroid.data.model.LenderType
import com.example.pesapilotandroid.ui.components.EmptyStateScreen
import com.example.pesapilotandroid.ui.components.LoadingScreen
import com.example.pesapilotandroid.ui.components.PesaPilotTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LenderDirectoryScreen(
    navController: NavController,
    viewModel: LenderDirectoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = "Lender Directory",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedType == null,
                        onClick = { viewModel.filterByType(null) },
                        label = { Text("All") }
                    )
                }
                items(LenderType.entries) { type ->
                    FilterChip(
                        selected = uiState.selectedType == type.value,
                        onClick = { viewModel.filterByType(type.value) },
                        label = { Text(type.displayName) }
                    )
                }
            }

            when {
                uiState.isLoading -> LoadingScreen()
                uiState.filteredLenders.isEmpty() -> {
                    EmptyStateScreen(
                        title = "No Lenders Found",
                        message = "Check back later for available lenders",
                        icon = Icons.Default.Business
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.filteredLenders) { lender ->
                            LenderCard(
                                lender = lender,
                                onVisitWebsite = { url ->
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LenderCard(
    lender: Lender,
    onVisitWebsite: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                // Logo
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(56.dp)
                ) {
                    if (lender.logoUrl != null) {
                        AsyncImage(
                            model = lender.logoUrl,
                            contentDescription = lender.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Business,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lender.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = LenderType.entries
                                .find { it.value == lender.lenderType }?.displayName 
                                ?: lender.lenderType,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    lender.description?.let { desc ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Loan info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Loan Range",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${formatCurrency(lender.minLoanAmount ?: 0.0)} - ${formatCurrency(lender.maxLoanAmount ?: 0.0)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column {
                    Text(
                        text = "Interest Rate",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${lender.minInterestRate ?: 0}% - ${lender.maxInterestRate ?: 0}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            lender.website?.let { website ->
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { onVisitWebsite(website) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Visit Website")
                }
            }
        }
    }
}

private fun formatCurrency(amount: Double): String {
    return when {
        amount >= 1000000 -> String.format("$%.1fM", amount / 1000000)
        amount >= 1000 -> String.format("$%.0fK", amount / 1000)
        else -> String.format("$%.0f", amount)
    }
}
