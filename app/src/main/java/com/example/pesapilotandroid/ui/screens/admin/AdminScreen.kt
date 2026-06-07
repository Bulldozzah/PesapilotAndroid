package com.example.pesapilotandroid.ui.screens.admin

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
fun AdminScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = "Admin Panel",
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
                Text(
                    text = "Manage app content and settings",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                MenuCard(
                    title = "Overview",
                    description = "View app statistics and metrics",
                    icon = Icons.Default.Dashboard,
                    onClick = { navController.navigate(NavRoute.AdminOverview) }
                )
            }

            item {
                MenuCard(
                    title = "Microfinance Management",
                    description = "Add and manage microfinance institutions",
                    icon = Icons.Default.AccountBalance,
                    onClick = { navController.navigate(NavRoute.AdminMicrofinance) }
                )
            }
        }
    }
}
