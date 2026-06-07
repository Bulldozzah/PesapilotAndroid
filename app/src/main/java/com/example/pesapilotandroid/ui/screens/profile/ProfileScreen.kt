package com.example.pesapilotandroid.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.pesapilotandroid.ui.components.MenuCard
import com.example.pesapilotandroid.ui.components.PesaPilotTopBar
import com.example.pesapilotandroid.ui.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            PesaPilotTopBar(title = "Profile")
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
                // Profile Header
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.extraLarge,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(80.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = uiState.profile?.fullName?.firstOrNull()?.uppercase() ?: "U",
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = uiState.profile?.fullName ?: "User",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            uiState.profile?.phone?.let { phone ->
                                Text(
                                    text = phone,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            uiState.profile?.businessName?.let { businessName ->
                                Text(
                                    text = businessName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Stats
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Businesses",
                            value = uiState.businessCount.toString(),
                            icon = Icons.Default.Business,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Savings Goals",
                            value = uiState.savingsGoalCount.toString(),
                            icon = Icons.Default.Savings,
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
                            title = "Country",
                            value = uiState.profile?.country ?: "N/A",
                            icon = Icons.Default.Public,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Currency",
                            value = uiState.profile?.currency ?: "N/A",
                            icon = Icons.Default.AttachMoney,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Actions
                item {
                    MenuCard(
                        title = "Edit Profile",
                        description = "Update your personal information",
                        icon = Icons.Default.Edit,
                        onClick = { navController.navigate(NavRoute.EditProfile) }
                    )
                }

                // Admin button (only for admins)
                if (uiState.isAdmin) {
                    item {
                        MenuCard(
                            title = "Admin Panel",
                            description = "Manage app content and users",
                            icon = Icons.Default.AdminPanelSettings,
                            onClick = { navController.navigate(NavRoute.Admin) }
                        )
                    }
                }

                item {
                    MenuCard(
                        title = "Logout",
                        description = "Sign out of your account",
                        icon = Icons.Default.Logout,
                        onClick = { showLogoutDialog = true }
                    )
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout(onLogout)
                    }
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
