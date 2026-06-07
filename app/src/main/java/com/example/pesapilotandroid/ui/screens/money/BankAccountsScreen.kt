package com.example.pesapilotandroid.ui.screens.money

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pesapilotandroid.data.model.BankAccount
import com.example.pesapilotandroid.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankAccountsScreen(
    navController: NavController,
    viewModel: BankAccountsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = "Bank Accounts",
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Account")
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingScreen()
            uiState.accounts.isEmpty() -> {
                EmptyStateScreen(
                    title = "No Bank Accounts",
                    message = "Add your business bank accounts to track balances",
                    icon = Icons.Default.AccountBalance,
                    actionText = "Add Account",
                    onAction = { showAddDialog = true }
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
                    items(uiState.accounts) { account ->
                        BankAccountCard(
                            account = account,
                            onDelete = { viewModel.deleteAccount(account.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBankAccountDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { bankName, accountName, accountNumber, currency, balance ->
                viewModel.createAccount(bankName, accountName, accountNumber, currency, balance)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun BankAccountCard(
    account: BankAccount,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.accountName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${account.bankName} • ${account.accountNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${account.currency} ${String.format("%,.2f", account.currentBalance)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun AddBankAccountDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, Double) -> Unit
) {
    var bankName by remember { mutableStateOf("") }
    var accountName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("USD") }
    var balance by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Bank Account") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PesaPilotTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = "Bank Name"
                )
                PesaPilotTextField(
                    value = accountName,
                    onValueChange = { accountName = it },
                    label = "Account Name"
                )
                PesaPilotTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = "Account Number"
                )
                PesaPilotTextField(
                    value = currency,
                    onValueChange = { currency = it },
                    label = "Currency"
                )
                PesaPilotTextField(
                    value = balance,
                    onValueChange = { balance = it },
                    label = "Current Balance",
                    keyboardType = KeyboardType.Decimal
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onAdd(bankName, accountName, accountNumber, currency, balance.toDoubleOrNull() ?: 0.0)
                },
                enabled = bankName.isNotBlank() && accountName.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
