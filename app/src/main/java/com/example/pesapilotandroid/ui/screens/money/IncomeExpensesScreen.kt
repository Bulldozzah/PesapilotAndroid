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
import com.example.pesapilotandroid.data.model.ExpenseCategory
import com.example.pesapilotandroid.data.model.IncomeFrequency
import com.example.pesapilotandroid.data.model.PersonalExpense
import com.example.pesapilotandroid.data.model.PersonalIncome
import com.example.pesapilotandroid.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeExpensesScreen(
    navController: NavController,
    viewModel: IncomeExpensesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = "Income & Expenses",
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Income") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Expenses") }
                )
            }

            when {
                uiState.isLoading -> LoadingScreen()
                selectedTab == 0 -> {
                    if (uiState.incomes.isEmpty()) {
                        EmptyStateScreen(
                            title = "No Income Records",
                            message = "Add your income sources to track earnings",
                            icon = Icons.Default.TrendingUp
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.incomes) { income ->
                                IncomeCard(
                                    income = income,
                                    currency = uiState.currency,
                                    onDelete = { viewModel.deleteIncome(income.id) }
                                )
                            }
                        }
                    }
                }
                else -> {
                    if (uiState.expenses.isEmpty()) {
                        EmptyStateScreen(
                            title = "No Expense Records",
                            message = "Track your expenses to manage spending",
                            icon = Icons.Default.TrendingDown
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.expenses) { expense ->
                                ExpenseCard(
                                    expense = expense,
                                    currency = uiState.currency,
                                    onDelete = { viewModel.deleteExpense(expense.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        if (selectedTab == 0) {
            AddIncomeDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { source, amount, frequency ->
                    viewModel.addIncome(source, amount, frequency)
                    showAddDialog = false
                }
            )
        } else {
            AddExpenseDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { category, amount, description ->
                    viewModel.addExpense(category, amount, description)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun IncomeCard(
    income: PersonalIncome,
    currency: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = income.source,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = income.frequency.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$currency ${String.format("%,.2f", income.amount)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
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
private fun ExpenseCard(
    expense: PersonalExpense,
    currency: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ExpenseCategory.entries.find { it.value == expense.category }?.displayName 
                        ?: expense.category,
                    style = MaterialTheme.typography.titleMedium
                )
                expense.description?.let { desc ->
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "$currency ${String.format("%,.2f", expense.amount)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddIncomeDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Double, String) -> Unit
) {
    var source by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf(IncomeFrequency.MONTHLY.value) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Income") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PesaPilotTextField(
                    value = source,
                    onValueChange = { source = it },
                    label = "Source"
                )
                PesaPilotTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "Amount",
                    keyboardType = KeyboardType.Decimal
                )
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = IncomeFrequency.entries.find { it.value == frequency }?.displayName ?: frequency,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Frequency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        IncomeFrequency.entries.forEach { freq ->
                            DropdownMenuItem(
                                text = { Text(freq.displayName) },
                                onClick = {
                                    frequency = freq.value
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    amount.toDoubleOrNull()?.let { amt ->
                        onAdd(source, amt, frequency)
                    }
                },
                enabled = source.isNotBlank() && amount.toDoubleOrNull() != null
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Double, String?) -> Unit
) {
    var category by remember { mutableStateOf(ExpenseCategory.OTHER.value) }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Expense") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = ExpenseCategory.entries.find { it.value == category }?.displayName ?: category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        ExpenseCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.displayName) },
                                onClick = {
                                    category = cat.value
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                PesaPilotTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "Amount",
                    keyboardType = KeyboardType.Decimal
                )
                PesaPilotTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description (Optional)"
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    amount.toDoubleOrNull()?.let { amt ->
                        onAdd(category, amt, description.ifBlank { null })
                    }
                },
                enabled = amount.toDoubleOrNull() != null
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
