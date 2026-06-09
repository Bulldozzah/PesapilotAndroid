package com.example.pesapilotandroid.ui.screens.money

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pesapilotandroid.data.model.*
import com.example.pesapilotandroid.navigation.NavRoute
import com.example.pesapilotandroid.ui.components.LoadingScreen
import com.example.pesapilotandroid.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountingScreen(
    navController: NavController,
    viewModel: AccountingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading && uiState.accounts.isEmpty()) {
        LoadingScreen()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Business selector ───────────────────────────────────────────────
        if (uiState.businesses.isNotEmpty()) {
            var expanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedCard(
                    onClick = { expanded = true },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Business",
                                fontFamily = FigtreeFamily,
                                fontSize = 11.sp,
                                color = AppMutedText
                            )
                            Text(
                                text = uiState.selectedBusiness?.name ?: "Select business",
                                fontFamily = OutfitFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = AppText
                            )
                        }
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = AppMutedText)
                    }
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    uiState.businesses.forEach { biz ->
                        DropdownMenuItem(
                            text = { Text(biz.name, fontFamily = FigtreeFamily) },
                            onClick = {
                                viewModel.selectBusiness(biz)
                                expanded = false
                            },
                            leadingIcon = {
                                if (biz.id == uiState.selectedBusiness?.id)
                                    Icon(Icons.Default.Check, contentDescription = null, tint = AppPrimary)
                            }
                        )
                    }
                }
            }
        }

        // ── Tab row ─────────────────────────────────────────────────────────
        ScrollableTabRow(
            selectedTabIndex = uiState.selectedTab.ordinal,
            containerColor = AppPrimary,
            contentColor = Color.White,
            edgePadding = 0.dp,
            divider = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            AccountingTab.entries.forEach { tab ->
                Tab(
                    selected = uiState.selectedTab == tab,
                    onClick = { viewModel.selectTab(tab) },
                    text = {
                        Text(
                            tab.label,
                            fontFamily = FigtreeFamily,
                            fontWeight = if (uiState.selectedTab == tab) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.White.copy(alpha = 0.65f)
                )
            }
        }

        // ── Tab content ─────────────────────────────────────────────────────
        when (uiState.selectedTab) {
            AccountingTab.TRIAL_BALANCE -> TrialBalanceContent(uiState)
            AccountingTab.JOURNAL_ENTRIES -> JournalEntriesContent(uiState, navController, viewModel)
            AccountingTab.GENERAL_LEDGER -> GeneralLedgerContent(uiState)
            AccountingTab.CHART_OF_ACCOUNTS -> ChartOfAccountsContent(uiState, viewModel)
        }
    }
}

// ─── Trial Balance ────────────────────────────────────────────────────────────

@Composable
private fun TrialBalanceContent(uiState: AccountingUiState) {
    val trialBalance = remember(uiState.entries, uiState.entryLines, uiState.accounts) {
        computeTrialBalance(uiState)
    }

    if (trialBalance.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No posted entries yet", fontFamily = FigtreeFamily, color = AppMutedText)
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppPrimary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text("Code", Modifier.weight(0.15f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = AppText)
                Text("Account", Modifier.weight(0.35f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = AppText)
                Text("Debit", Modifier.weight(0.25f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = AppText)
                Text("Credit", Modifier.weight(0.25f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = AppText)
            }
        }

        items(trialBalance) { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 4.dp)
            ) {
                Text(row.code, Modifier.weight(0.15f), fontFamily = FigtreeFamily, fontSize = 11.sp, color = AppMutedText)
                Text(row.name, Modifier.weight(0.35f), fontFamily = FigtreeFamily, fontSize = 11.sp, color = AppText)
                Text(row.debitStr, Modifier.weight(0.25f), fontFamily = FigtreeFamily, fontSize = 11.sp, color = AppText)
                Text(row.creditStr, Modifier.weight(0.25f), fontFamily = FigtreeFamily, fontSize = 11.sp, color = AppText)
            }
        }

        // Totals
        val totalDebit = trialBalance.sumOf { it.debit }
        val totalCredit = trialBalance.sumOf { it.credit }
        val balanced = kotlin.math.abs(totalDebit - totalCredit) < 0.01

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (balanced) AppGreenSuccess.copy(alpha = 0.1f) else AppRedDestructive.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text("TOTAL", Modifier.weight(0.5f), fontFamily = OutfitFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AppText)
                Text(fmtAmount(totalDebit), Modifier.weight(0.25f), fontFamily = OutfitFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AppText)
                Text(fmtAmount(totalCredit), Modifier.weight(0.25f), fontFamily = OutfitFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AppText)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (balanced) "✅ Books are balanced" else "⚠️ Out of balance by ${fmtAmount(kotlin.math.abs(totalDebit - totalCredit))}",
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = if (balanced) AppGreenSuccess else AppRedDestructive
            )
        }
    }
}

// ─── Journal Entries ──────────────────────────────────────────────────────────

@Composable
private fun JournalEntriesContent(
    uiState: AccountingUiState,
    navController: NavController,
    viewModel: AccountingViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { navController.navigate(NavRoute.JournalEntryDetail(null)) },
                    colors = ButtonDefaults.buttonColors(containerColor = AppPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("New Entry", fontFamily = FigtreeFamily, fontSize = 13.sp)
                }
                OutlinedButton(
                    onClick = { viewModel.refresh() },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Refresh", fontFamily = FigtreeFamily, fontSize = 13.sp)
                }
            }
        }

        if (uiState.entries.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No journal entries yet", fontFamily = FigtreeFamily, color = AppMutedText)
                }
            }
        }

        items(uiState.entries) { entry ->
            val lines = uiState.entryLines.filter { it.journalEntryId == entry.id }
            JournalEntryCard(entry = entry, lines = lines, navController = navController)
        }
    }
}

@Composable
private fun JournalEntryCard(entry: JournalEntry, lines: List<JournalEntryLine>, navController: NavController) {
    Card(
        onClick = { navController.navigate(NavRoute.JournalEntryDetail(entry.id)) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.reference ?: "—",
                    fontFamily = FigtreeFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = AppText
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (entry.isPosted) AppGreenSuccess.copy(alpha = 0.15f) else AppAmberWarning.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (entry.isPosted) "Posted" else "Draft",
                        fontFamily = FigtreeFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        color = if (entry.isPosted) AppGreenSuccess else AppAmberWarning,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            if (!entry.description.isNullOrBlank()) {
                Text(
                    text = entry.description,
                    fontFamily = FigtreeFamily,
                    fontSize = 11.sp,
                    color = AppMutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Text(
                text = entry.entryDate,
                fontFamily = FigtreeFamily,
                fontSize = 10.sp,
                color = AppMutedText,
                modifier = Modifier.padding(top = 2.dp)
            )

            if (lines.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = AppBorder)
                Spacer(modifier = Modifier.height(4.dp))
                lines.forEach { line ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = line.memo ?: "—",
                            fontFamily = FigtreeFamily,
                            fontSize = 10.sp,
                            color = AppMutedText,
                            modifier = Modifier.weight(1f)
                        )
                        if (line.debit > 0) {
                            Text(
                                text = fmtAmount(line.debit),
                                fontFamily = FigtreeFamily,
                                fontSize = 10.sp,
                                color = AppGreenSuccess
                            )
                        }
                        if (line.credit > 0) {
                            Text(
                                text = fmtAmount(line.credit),
                                fontFamily = FigtreeFamily,
                                fontSize = 10.sp,
                                color = AppRedDestructive
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── General Ledger ───────────────────────────────────────────────────────────

@Composable
private fun GeneralLedgerContent(uiState: AccountingUiState) {
    var selectedAccountId by remember { mutableStateOf<String?>(null) }

    Row(modifier = Modifier.fillMaxSize()) {
        // Account list (left panel)
        LazyColumn(
            modifier = Modifier
                .width(140.dp)
                .fillMaxHeight()
                .background(AppCard)
                .padding(8.dp)
        ) {
            val grouped = uiState.accounts.groupBy { it.accountType ?: it.type }
            grouped.forEach { (type, accounts) ->
                item {
                    Text(
                        text = type.replaceFirstChar { it.uppercase() },
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = AppPrimary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    )
                }
                items(accounts) { acct ->
                    val isSelected = selectedAccountId == acct.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AppPrimary.copy(alpha = 0.1f) else Color.Transparent)
                            .clickable { selectedAccountId = acct.id }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${acct.code} ${acct.name}",
                            fontFamily = FigtreeFamily,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 10.sp,
                            color = if (isSelected) AppPrimary else AppText,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Ledger table (right panel)
        val selectedAccount = uiState.accounts.find { it.id == selectedAccountId }
        if (selectedAccount == null) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                Text("Select an account", fontFamily = FigtreeFamily, color = AppMutedText)
            }
        } else {
            val ledgerRows = remember(uiState.entries, uiState.entryLines, selectedAccountId) {
                computeLedger(uiState, selectedAccountId!!)
            }
            LazyColumn(modifier = Modifier.weight(1f).fillMaxHeight().padding(8.dp)) {
                item {
                    Text(
                        text = "${selectedAccount.code} — ${selectedAccount.name}",
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AppText,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(ledgerRows) { row ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                        Text(row.date, Modifier.weight(0.18f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppMutedText)
                        Text(row.ref, Modifier.weight(0.18f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppMutedText)
                        Text(row.desc, Modifier.weight(0.24f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppMutedText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(row.debitStr, Modifier.weight(0.15f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppGreenSuccess)
                        Text(row.creditStr, Modifier.weight(0.15f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppRedDestructive)
                        Text(row.balanceStr, Modifier.weight(0.1f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
                    }
                }
            }
        }
    }
}

// ─── Chart of Accounts ────────────────────────────────────────────────────────

@Composable
private fun ChartOfAccountsContent(uiState: AccountingUiState, viewModel: AccountingViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.seedDefaults() },
                    colors = ButtonDefaults.buttonColors(containerColor = AppPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Initialize Defaults", fontFamily = FigtreeFamily, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        val grouped = uiState.accounts.groupBy { it.accountType ?: it.type }
        grouped.forEach { (type, accounts) ->
            item {
                Text(
                    text = type.replaceFirstChar { it.uppercase() },
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = AppPrimary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                )
            }
            items(accounts) { acct ->
                val balance = remember(uiState.entryLines) {
                    val lines = uiState.entryLines.filter { it.accountId == acct.id }
                    lines.sumOf { it.debit - it.credit }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppCard)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${acct.code} — ${acct.name}",
                            fontFamily = FigtreeFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = AppText
                        )
                        acct.subcategory?.let {
                            Text(it, fontFamily = FigtreeFamily, fontSize = 10.sp, color = AppMutedText)
                        }
                    }
                    Text(
                        text = fmtAmount(balance),
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = if (balance >= 0) AppGreenSuccess else AppRedDestructive
                    )
                }
            }
        }
    }
}

// ─── Computation helpers ──────────────────────────────────────────────────────

private data class TrialBalanceRow(
    val code: String, val name: String,
    val debit: Double, val credit: Double,
    val debitStr: String, val creditStr: String
)

private fun computeTrialBalance(uiState: AccountingUiState): List<TrialBalanceRow> {
    val accountMap = uiState.accounts.associateBy { it.id }
    val postedEntryIds = uiState.entries.filter { it.isPosted }.map { it.id }.toSet()
    val postedLines = uiState.entryLines.filter { it.journalEntryId in postedEntryIds }

    val totals = mutableMapOf<String, Double>() // accountId -> net (debit - credit)
    postedLines.forEach { line ->
        totals[line.accountId] = (totals[line.accountId] ?: 0.0) + line.debit - line.credit
    }

    return totals.mapNotNull { (acctId, net) ->
        val acct = accountMap[acctId] ?: return@mapNotNull null
        if (kotlin.math.abs(net) < 0.005) return@mapNotNull null
        TrialBalanceRow(
            code = acct.code, name = acct.name,
            debit = if (net > 0) net else 0.0,
            credit = if (net < 0) -net else 0.0,
            debitStr = if (net > 0) fmtAmount(net) else "",
            creditStr = if (net < 0) fmtAmount(-net) else ""
        )
    }.sortedBy { it.code }
}

private data class LedgerRow(
    val date: String, val ref: String, val desc: String,
    val debit: Double, val credit: Double,
    val debitStr: String, val creditStr: String,
    val balance: Double, val balanceStr: String
)

private fun computeLedger(uiState: AccountingUiState, accountId: String): List<LedgerRow> {
    val postedEntries = uiState.entries.filter { it.isPosted }.sortedBy { it.entryDate }
    val linesByEntry = uiState.entryLines.groupBy { it.journalEntryId }
    var running = 0.0

    return postedEntries.flatMap { entry ->
        val lines = linesByEntry[entry.id].orEmpty().filter { it.accountId == accountId }
        lines.map { line ->
            running += line.debit - line.credit
            LedgerRow(
                date = entry.entryDate,
                ref = entry.reference ?: "",
                desc = entry.description ?: "",
                debit = line.debit, credit = line.credit,
                debitStr = if (line.debit > 0) fmtAmount(line.debit) else "",
                creditStr = if (line.credit > 0) fmtAmount(line.credit) else "",
                balance = running, balanceStr = fmtAmount(running)
            )
        }
    }
}

private fun fmtAmount(v: Double): String = String.format(java.util.Locale.US, "%,.2f", v)
