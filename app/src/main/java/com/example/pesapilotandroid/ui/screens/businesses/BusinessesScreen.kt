package com.example.pesapilotandroid.ui.screens.businesses

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
fun BusinessesScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            PesaPilotTopBar(title = "Businesses")
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
                    text = "Start & Grow",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                MenuCard(
                    title = "Business Discovery",
                    description = "Explore business ideas and templates",
                    icon = Icons.Default.Lightbulb,
                    onClick = { navController.navigate(NavRoute.BusinessDiscovery) }
                )
            }

            item {
                MenuCard(
                    title = "My Businesses",
                    description = "Manage your businesses and track progress",
                    icon = Icons.Default.Business,
                    onClick = { navController.navigate(NavRoute.MyBusinesses) }
                )
            }

            item {
                MenuCard(
                    title = "Roadmaps",
                    description = "Step-by-step business setup guides",
                    icon = Icons.Default.Map,
                    onClick = { navController.navigate(NavRoute.Roadmaps) }
                )
            }

            item {
                MenuCard(
                    title = "Business Plans",
                    description = "Create and manage business plans",
                    icon = Icons.Default.Description,
                    onClick = { navController.navigate(NavRoute.BusinessPlans) }
                )
            }
        }
    }
}
