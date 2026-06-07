package com.example.pesapilotandroid.ui.screens.businesses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pesapilotandroid.ui.components.PesaPilotButton
import com.example.pesapilotandroid.ui.components.PesaPilotTextField
import com.example.pesapilotandroid.ui.components.PesaPilotTopBar
import com.example.pesapilotandroid.ui.components.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessPlanDetailScreen(
    planId: String?,
    navController: NavController,
    viewModel: BusinessPlanDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(planId) {
        if (planId != null) {
            viewModel.loadPlan(planId)
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = if (planId == null) "Create Business Plan" else "Edit Business Plan",
                onBackClick = { navController.popBackStack() },
                actions = {
                    if (planId != null) {
                        IconButton(onClick = { viewModel.deletePlan() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PesaPilotTextField(
                    value = uiState.businessName,
                    onValueChange = { viewModel.updateBusinessName(it) },
                    label = "Business Plan Title *"
                )

                PesaPilotTextField(
                    value = uiState.content,
                    onValueChange = { viewModel.updateContent(it) },
                    label = "Plan Content (JSON)",
                    singleLine = false,
                    maxLines = 10
                )

                Spacer(modifier = Modifier.height(16.dp))

                PesaPilotButton(
                    text = if (planId == null) "Create Plan" else "Save Changes",
                    onClick = { viewModel.savePlan() },
                    isLoading = uiState.isSaving,
                    enabled = uiState.businessName.isNotBlank()
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
