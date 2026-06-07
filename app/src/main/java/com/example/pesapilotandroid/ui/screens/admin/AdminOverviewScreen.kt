package com.example.pesapilotandroid.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pesapilotandroid.ui.components.LoadingScreen
import com.example.pesapilotandroid.ui.components.PesaPilotTopBar
import com.example.pesapilotandroid.ui.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOverviewScreen(
    navController: NavController,
    viewModel: AdminOverviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = "Admin Overview",
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
                item {
                    Text(
                        text = "Reference Data",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Business Templates",
                            value = uiState.templateCount.toString(),
                            icon = Icons.Default.Lightbulb,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Categories",
                            value = uiState.categoryCount.toString(),
                            icon = Icons.Default.Category,
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
                            title = "Lenders",
                            value = uiState.lenderCount.toString(),
                            icon = Icons.Default.Business,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Microfinance",
                            value = uiState.microfinanceCount.toString(),
                            icon = Icons.Default.AccountBalance,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
