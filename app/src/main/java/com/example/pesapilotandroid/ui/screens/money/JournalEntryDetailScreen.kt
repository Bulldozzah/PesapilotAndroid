package com.example.pesapilotandroid.ui.screens.money

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pesapilotandroid.data.model.ChartOfAccount
import com.example.pesapilotandroid.data.model.Contact
import com.example.pesapilotandroid.ui.components.LoadingScreen
import com.example.pesapilotandroid.ui.theme.*

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
        if (entryId != null) viewModel.loadEntry(entryId)
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) navController.popBackStack()
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title
                Text(
                    text = if (entryId == null) "New Journal Entry" else "Edit Journal Entry",
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AppText
                )

                // Business selector
                if (uiState.businesses.isNotEmpty()) {
                    var bizExpanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedCard(
                            onClick = { bizExpanded = true },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uiState.selectedBusiness?.name ?: "Select business",
                                    fontFamily = FigtreeFamily,
                                    fontSize = 13.sp,
                                    color = AppText
                                )
                                Icon(Icons.Default.ArrowDropDown, null, tint = AppMutedText)
                            }
                        }
                        DropdownMenu(expanded = bizExpanded, onDismissRequest = { bizExpanded = false }) {
                            uiState.businesses.forEach { biz ->
                                DropdownMenuItem(
                                    text = { Text(biz.name) },
                                    onClick = { viewModel.onBusinessSelected(biz); bizExpanded = false }
                                )
                            }
                        }
                    }
                }

                // Header fields
                OutlinedTextField(
                    value = uiState.referenceNumber,
                    onValueChange = { viewModel.updateReferenceNumber(it) },
                    label = { Text("Reference", fontFamily = FigtreeFamily) },
                    placeholder = { Text("Auto-generated if blank", fontFamily = FigtreeFamily, fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.entryDate,
                    onValueChange = { viewModel.updateEntryDate(it) },
                    label = { Text("Date (YYYY-MM-DD)", fontFamily = FigtreeFamily) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = { Text("Description *", fontFamily = FigtreeFamily) },
                    singleLine = false,
                    maxLines = 3,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(color = AppBorder)

                // Lines header
                Row(
                    modifier = Modifier.fillMaxWidth().background(AppPrimary.copy(alpha = 0.08f), RoundedCornerShape(8.dp)).padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Account", Modifier.weight(0.35f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, color = AppText)
                    Text("Debit", Modifier.weight(0.17f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, color = AppText)
                    Text("Credit", Modifier.weight(0.17f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, color = AppText)
                    Text("Memo", Modifier.weight(0.23f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, color = AppText)
                    Spacer(Modifier.width(24.dp))
                }

                // Lines
                uiState.lines.forEachIndexed { index, line ->
                    val selectedAccount = uiState.accounts.find { it.id == line.accountId }
                    var acctExpanded by remember { mutableStateOf(false) }

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Account dropdown
                            Box(modifier = Modifier.weight(0.35f)) {
                                OutlinedCard(
                                    onClick = { acctExpanded = true },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = selectedAccount?.let { "${it.code} ${it.name}" } ?: "Select account",
                                        fontFamily = FigtreeFamily,
                                        fontSize = 10.sp,
                                        color = if (selectedAccount != null) AppText else AppMutedText,
                                        maxLines = 1,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                                DropdownMenu(expanded = acctExpanded, onDismissRequest = { acctExpanded = false }) {
                                    val grouped = uiState.accounts.groupBy { it.accountType ?: it.type }
                                    grouped.forEach { (type, accounts) ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    type.replaceFirstChar { it.uppercase() },
                                                    fontFamily = OutfitFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = AppPrimary
                                                )
                                            },
                                            onClick = {},
                                            enabled = false
                                        )
                                        accounts.forEach { acct ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        "${acct.code} ${acct.name}",
                                                        fontFamily = FigtreeFamily,
                                                        fontSize = 11.sp
                                                    )
                                                },
                                                onClick = {
                                                    viewModel.updateLineAccount(index, acct.id)
                                                    acctExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Debit
                            OutlinedTextField(
                                value = line.debit,
                                onValueChange = { v -> viewModel.updateLineDebit(index, v) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(0.17f)
                            )

                            // Credit
                            OutlinedTextField(
                                value = line.credit,
                                onValueChange = { v -> viewModel.updateLineCredit(index, v) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(0.17f)
                            )

                            // Memo
                            OutlinedTextField(
                                value = line.memo,
                                onValueChange = { v -> viewModel.updateLineMemo(index, v) },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(0.23f)
                            )

                            // Remove
                            IconButton(
                                onClick = { viewModel.removeLine(index) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, "Remove", tint = AppRedDestructive, modifier = Modifier.size(16.dp))
                            }
                        }

                        // Vendor/Customer row for A/P or A/R
                        val acctName = selectedAccount?.name?.lowercase() ?: ""
                        if (acctName.contains("payable") || acctName.contains("a/p")) {
                            var vendExpanded by remember { mutableStateOf(false) }
                            val vendors = uiState.contacts.filter { it.type == "vendor" }
                            Box(modifier = Modifier.padding(start = 4.dp, top = 2.dp)) {
                                OutlinedCard(
                                    onClick = { vendExpanded = true },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    val vendor = vendors.find { it.id == line.vendorId }
                                    Text(
                                        text = vendor?.name ?: "Select vendor",
                                        fontFamily = FigtreeFamily,
                                        fontSize = 9.sp,
                                        color = if (vendor != null) AppText else AppMutedText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                DropdownMenu(expanded = vendExpanded, onDismissRequest = { vendExpanded = false }) {
                                    vendors.forEach { v ->
                                        DropdownMenuItem(
                                            text = { Text(v.name, fontSize = 11.sp) },
                                            onClick = { viewModel.updateLineVendor(index, v.id); vendExpanded = false }
                                        )
                                    }
                                }
                            }
                        }
                        if (acctName.contains("receivable") || acctName.contains("a/r")) {
                            var custExpanded by remember { mutableStateOf(false) }
                            val customers = uiState.contacts.filter { it.type == "customer" }
                            Box(modifier = Modifier.padding(start = 4.dp, top = 2.dp)) {
                                OutlinedCard(
                                    onClick = { custExpanded = true },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    val customer = customers.find { it.id == line.customerId }
                                    Text(
                                        text = customer?.name ?: "Select customer",
                                        fontFamily = FigtreeFamily,
                                        fontSize = 9.sp,
                                        color = if (customer != null) AppText else AppMutedText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                DropdownMenu(expanded = custExpanded, onDismissRequest = { custExpanded = false }) {
                                    customers.forEach { c ->
                                        DropdownMenuItem(
                                            text = { Text(c.name, fontSize = 11.sp) },
                                            onClick = { viewModel.updateLineCustomer(index, c.id); custExpanded = false }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Add line button
                OutlinedButton(
                    onClick = { viewModel.addLine() },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Add Line", fontFamily = FigtreeFamily, fontSize = 12.sp)
                }

                // Balance summary
                val totalDebit = uiState.lines.sumOf { it.debit.toDoubleOrNull() ?: 0.0 }
                val totalCredit = uiState.lines.sumOf { it.credit.toDoubleOrNull() ?: 0.0 }
                val diff = kotlin.math.abs(totalDebit - totalCredit)
                val isBalanced = diff < 0.01 && (totalDebit > 0 || totalCredit > 0)

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isBalanced) AppGreenSuccess.copy(alpha = 0.1f) else AppRedDestructive.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Debits", fontFamily = FigtreeFamily, fontSize = 10.sp, color = AppMutedText)
                            Text(fmtAmount(totalDebit), fontFamily = OutfitFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AppText)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Status", fontFamily = FigtreeFamily, fontSize = 10.sp, color = AppMutedText)
                            Text(
                                text = if (isBalanced) "✓ Balanced" else "⚠ Out by ${fmtAmount(diff)}",
                                fontFamily = FigtreeFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = if (isBalanced) AppGreenSuccess else AppRedDestructive
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Credits", fontFamily = FigtreeFamily, fontSize = 10.sp, color = AppMutedText)
                            Text(fmtAmount(totalCredit), fontFamily = OutfitFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AppText)
                        }
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.saveEntry(post = false) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        enabled = uiState.description.isNotBlank() && uiState.lines.any { it.accountId.isNotBlank() }
                    ) {
                        Text("Save Draft", fontFamily = FigtreeFamily, fontSize = 13.sp)
                    }
                    Button(
                        onClick = { viewModel.saveEntry(post = true) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        enabled = isBalanced && uiState.description.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = AppPrimary)
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Save & Post", fontFamily = FigtreeFamily, fontSize = 13.sp)
                        }
                    }
                }

                // Delete button for existing entries
                if (entryId != null) {
                    OutlinedButton(
                        onClick = { viewModel.deleteEntry() },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AppRedDestructive)
                    ) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Delete Entry", fontFamily = FigtreeFamily, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

private fun fmtAmount(v: Double): String = String.format(java.util.Locale.US, "%,.2f", v)
