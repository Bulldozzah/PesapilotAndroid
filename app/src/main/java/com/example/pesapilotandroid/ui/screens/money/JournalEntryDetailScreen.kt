package com.example.pesapilotandroid.ui.screens.money

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
import com.example.pesapilotandroid.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntryDetailScreen(
    entryId: String?,
    navController: NavController,
    viewModel: JournalEntryDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(entryId) {
        if (entryId != null) {
            viewModel.loadEntry(entryId)
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
                title = if (entryId == null) "New Journal Entry" else "Edit Entry",
                onBackClick = { navController.popBackStack() },
                actions = {
                    if (entryId != null) {
                        IconButton(onClick = { viewModel.deleteEntry() }) {
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
                    value = uiState.referenceNumber,
                    onValueChange = { viewModel.updateReferenceNumber(it) },
                    label = "Reference Number *"
                )

                PesaPilotTextField(
                    value = uiState.entryDate,
                    onValueChange = { viewModel.updateEntryDate(it) },
                    label = "Date (YYYY-MM-DD) *"
                )

                PesaPilotTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = "Description *",
                    singleLine = false,
                    maxLines = 3
                )

                HorizontalDivider()

                Text(
                    text = "Debit Lines",
                    style = MaterialTheme.typography.titleMedium
                )

                uiState.debitLines.forEachIndexed { index, line ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PesaPilotTextField(
                            value = line.accountName,
                            onValueChange = { viewModel.updateDebitLine(index, it, line.amount) },
                            label = "Account",
                            modifier = Modifier.weight(1f)
                        )
                        PesaPilotTextField(
                            value = line.amount,
                            onValueChange = { viewModel.updateDebitLine(index, line.accountName, it) },
                            label = "Amount",
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.width(120.dp)
                        )
                        IconButton(onClick = { viewModel.removeDebitLine(index) }) {
                            Icon(Icons.Default.Remove, contentDescription = "Remove")
                        }
                    }
                }

                OutlinedButton(
                    onClick = { viewModel.addDebitLine() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Debit Line")
                }

                HorizontalDivider()

                Text(
                    text = "Credit Lines",
                    style = MaterialTheme.typography.titleMedium
                )

                uiState.creditLines.forEachIndexed { index, line ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PesaPilotTextField(
                            value = line.accountName,
                            onValueChange = { viewModel.updateCreditLine(index, it, line.amount) },
                            label = "Account",
                            modifier = Modifier.weight(1f)
                        )
                        PesaPilotTextField(
                            value = line.amount,
                            onValueChange = { viewModel.updateCreditLine(index, line.accountName, it) },
                            label = "Amount",
                            keyboardType = KeyboardType.Decimal,
                            modifier = Modifier.width(120.dp)
                        )
                        IconButton(onClick = { viewModel.removeCreditLine(index) }) {
                            Icon(Icons.Default.Remove, contentDescription = "Remove")
                        }
                    }
                }

                OutlinedButton(
                    onClick = { viewModel.addCreditLine() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Credit Line")
                }

                HorizontalDivider()

                // Balance check
                val totalDebits = uiState.debitLines.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
                val totalCredits = uiState.creditLines.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
                val isBalanced = totalDebits == totalCredits && totalDebits > 0

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBalanced) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Debits", style = MaterialTheme.typography.labelMedium)
                            Text(
                                String.format("%.2f", totalDebits),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Column {
                            Text("Total Credits", style = MaterialTheme.typography.labelMedium)
                            Text(
                                String.format("%.2f", totalCredits),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Column {
                            Text("Status", style = MaterialTheme.typography.labelMedium)
                            Text(
                                if (isBalanced) "Balanced" else "Unbalanced",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isBalanced) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                PesaPilotButton(
                    text = if (entryId == null) "Create Entry" else "Save Changes",
                    onClick = { viewModel.saveEntry() },
                    isLoading = uiState.isSaving,
                    enabled = isBalanced && uiState.referenceNumber.isNotBlank() && 
                             uiState.description.isNotBlank()
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
