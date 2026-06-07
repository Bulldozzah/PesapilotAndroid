package com.example.pesapilotandroid.ui.screens.funding

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
fun FundingScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            PesaPilotTopBar(title = "Funding")
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
                    text = "Find funding for your business",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                MenuCard(
                    title = "Microfinance",
                    description = "Connect with microfinance institutions",
                    icon = Icons.Default.AccountBalance,
                    onClick = { navController.navigate(NavRoute.Microfinance) }
                )
            }

            item {
                MenuCard(
                    title = "Lender Directory",
                    description = "Browse banks, SACCOs, and other lenders",
                    icon = Icons.Default.Business,
                    onClick = { navController.navigate(NavRoute.LenderDirectory) }
                )
            }
        }
    }
}
