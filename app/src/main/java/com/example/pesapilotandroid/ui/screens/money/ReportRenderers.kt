package com.example.pesapilotandroid.ui.screens.money

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pesapilotandroid.ui.theme.*

// ─── 1. Income Statement ──────────────────────────────────────────────────

@Composable
fun IncomeStatementReport(viewModel: ReportsViewModel, from: String, to: String) {
    val data = remember { viewModel.computePnL(from, to) }

    Column(modifier = Modifier.fillMaxWidth()) {
        CollapsibleReportSection("Revenue", fmt(data.revenueTotal)) {
            data.revenueRows.forEach { row -> ReportRow(row.label, fmt(row.value)) }
            ReportRow("Total Revenue", fmt(data.revenueTotal), isBold = true)
        }

        if (data.cogsRows.isNotEmpty()) {
            CollapsibleReportSection("Cost of Goods Sold", fmt(data.cogsTotal)) {
                data.cogsRows.forEach { row -> ReportRow(row.label, fmt(row.value)) }
                ReportRow("Total COGS", fmt(data.cogsTotal), isBold = true)
            }
        }

        ReportRow("Gross Profit", fmt(data.grossProfit), isBold = true,
            isSuccess = data.grossProfit >= 0, isDestructive = data.grossProfit < 0)

        CollapsibleReportSection("Operating Expenses", fmt(data.expenseTotal)) {
            data.expenseRows.forEach { row -> ReportRow(row.label, fmt(row.value)) }
            ReportRow("Total Expenses", fmt(data.expenseTotal), isBold = true)
        }

        ReportRow("Operating Income", fmt(data.operatingIncome), isBold = true,
            isSuccess = data.operatingIncome >= 0, isDestructive = data.operatingIncome < 0)

        if (data.otherIncomeRows.isNotEmpty()) {
            CollapsibleReportSection("Other Income", fmt(data.otherIncomeTotal)) {
                data.otherIncomeRows.forEach { row -> ReportRow(row.label, fmt(row.value)) }
                ReportRow("Total Other Income", fmt(data.otherIncomeTotal), isBold = true)
            }
        }

        if (data.otherExpenseRows.isNotEmpty()) {
            CollapsibleReportSection("Other Expenses", fmt(data.otherExpenseTotal)) {
                data.otherExpenseRows.forEach { row -> ReportRow(row.label, fmt(row.value)) }
                ReportRow("Total Other Expenses", fmt(data.otherExpenseTotal), isBold = true)
            }
        }

        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (data.netIncome >= 0) AppGreenSuccess.copy(alpha = 0.1f)
                else AppRedDestructive.copy(alpha = 0.1f)
            )
        ) {
            ReportRow(
                label = if (data.netIncome >= 0) "Net Profit" else "Net Loss",
                value = fmt(kotlin.math.abs(data.netIncome)),
                isBold = true,
                isSuccess = data.netIncome >= 0,
                isDestructive = data.netIncome < 0
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            ChartCard("P&L Summary", Modifier.weight(1f)) {
                BarChart(
                    data = listOf(
                        "Revenue" to data.revenueTotal,
                        "COGS" to data.cogsTotal,
                        "Expenses" to data.expenseTotal,
                        "Net" to kotlin.math.abs(data.netIncome)
                    ),
                    barColor = AppPrimary
                )
            }

            val pieData = (data.expenseRows.map { row ->
                ReportsViewModel.ChartData(row.label, row.value)
            }).sortedByDescending { it.value }.take(6)
            if (pieData.isNotEmpty()) {
                Spacer(Modifier.width(12.dp))
                ChartCard("Expense Breakdown", Modifier.weight(1f)) {
                    PieChart(data = pieData)
                }
            }
        }
    }
}

// ─── 2. Balance Sheet ─────────────────────────────────────────────────────

@Composable
fun BalanceSheetReport(viewModel: ReportsViewModel, endDate: String, from: String, to: String) {
    val data = remember { viewModel.computeBalanceSheet(endDate, from, to) }

    Column(modifier = Modifier.fillMaxWidth()) {
        CollapsibleReportSection("Assets", fmt(data.totalAssets)) {
            data.assetRows.forEach { row -> ReportRow(row.label, fmt(row.value)) }
            ReportRow("Total Assets", fmt(data.totalAssets), isBold = true)
        }

        CollapsibleReportSection("Liabilities", fmt(data.totalLiabilities)) {
            data.liabilityRows.forEach { row -> ReportRow(row.label, fmt(row.value)) }
            ReportRow("Total Liabilities", fmt(data.totalLiabilities), isBold = true)
        }

        CollapsibleReportSection("Equity", fmt(data.totalEquity)) {
            data.equityRows.forEach { row -> ReportRow(row.label, fmt(row.value)) }
            ReportRow("Current Period Net Income", fmt(data.netIncome), indent = true,
                isSuccess = data.netIncome >= 0, isDestructive = data.netIncome < 0)
            ReportRow("Total Equity", fmt(data.totalEquity), isBold = true)
        }

        ReportRow("Total L + E", fmt(data.totalLiabilities + data.totalEquity), isBold = true)

        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (data.isBalanced) AppGreenSuccess.copy(alpha = 0.1f)
                else AppRedDestructive.copy(alpha = 0.1f)
            )
        ) {
            Text(
                text = if (data.isBalanced) "✓ A = L + E — Balanced"
                else "⚠ Balance sheet does not balance",
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = if (data.isBalanced) AppGreenSuccess else AppRedDestructive,
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            ChartCard("A = L + E", Modifier.weight(1f)) {
                BarChart(
                    data = listOf(
                        "Assets" to data.totalAssets,
                        "Liabilities" to data.totalLiabilities,
                        "Equity" to data.totalEquity
                    ),
                    barColor = AppAccentBlue
                )
            }

            val pieData = data.assetRows.filter { it.value > 0 }.map { row ->
                ReportsViewModel.ChartData(row.label, row.value)
            }.sortedByDescending { it.value }.take(6)
            if (pieData.isNotEmpty()) {
                Spacer(Modifier.width(12.dp))
                ChartCard("Asset Composition", Modifier.weight(1f)) {
                    PieChart(data = pieData)
                }
            }
        }
    }
}

// ─── 3. Cash Flow ─────────────────────────────────────────────────────────

@Composable
fun CashFlowReport(viewModel: ReportsViewModel, from: String, to: String) {
    val data = remember { viewModel.computeCashFlow(from, to) }

    Column(modifier = Modifier.fillMaxWidth()) {
        ReportRow("Opening Cash", fmt(data.openingCash), isBold = true)

        CashFlowActivitySection("Operating", data.operating, data.operatingInflows, data.operatingOutflows, AppAccentBlue)
        CashFlowActivitySection("Investing", data.investing, data.investingInflows, data.investingOutflows, AppGreenSuccess)
        CashFlowActivitySection("Financing", data.financing, data.financingInflows, data.financingOutflows, AppAmberWarning)

        ReportRow("Closing Cash", fmt(data.closingCash), isBold = true, isSuccess = true)

        Spacer(Modifier.height(16.dp))

        ChartCard("Cash Flow by Activity") {
            BarChart(
                data = listOf(
                    "Operating" to data.operating,
                    "Investing" to data.investing,
                    "Financing" to data.financing
                ),
                barColor = AppGreenSuccess
            )
        }
    }
}

@Composable
private fun CashFlowActivitySection(
    title: String,
    net: Double,
    inflows: List<Pair<String, Double>>,
    outflows: List<Pair<String, Double>>,
    borderColor: Color
) {
    var expanded by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .padding(bottom = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(borderColor.copy(alpha = 0.06f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(borderColor.copy(alpha = 0.12f))
                .clickable { expanded = !expanded }
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    null,
                    tint = AppMutedText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "$title Activities",
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = AppText
                )
            }
            Text(
                text = fmt(net),
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = if (net >= 0) AppGreenSuccess else AppRedDestructive
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 4.dp)) {
                inflows.forEach { (name, amount) ->
                    ReportRow("+ $name", fmt(amount), isSuccess = true)
                }
                outflows.forEach { (name, amount) ->
                    ReportRow("− $name", fmt(amount), isDestructive = true)
                }
                ReportRow("Net $title", fmt(net), isBold = true,
                    isSuccess = net >= 0, isDestructive = net < 0)
            }
        }
    }
}

// ─── 4. Trial Balance ─────────────────────────────────────────────────────

@Composable
fun TrialBalanceReport(viewModel: ReportsViewModel, endDate: String) {
    val data = remember { viewModel.computeTrialBalance(endDate) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppPrimary.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Text("Code", Modifier.weight(0.15f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, color = AppText)
            Text("Account", Modifier.weight(0.35f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, color = AppText)
            Text("Debit", Modifier.weight(0.25f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, color = AppText)
            Text("Credit", Modifier.weight(0.25f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, color = AppText)
        }

        data.rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (row.isAbnormal) Modifier.background(AppAmberWarning.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        else Modifier
                    )
                    .padding(vertical = 3.dp, horizontal = 4.dp)
            ) {
                Text(row.code, Modifier.weight(0.15f), fontFamily = FigtreeFamily, fontSize = 10.sp, color = AppMutedText)
                Row(modifier = Modifier.weight(0.35f), verticalAlignment = Alignment.CenterVertically) {
                    Text(row.name, fontFamily = FigtreeFamily, fontSize = 10.sp, color = AppText)
                    if (row.isAbnormal) Text(" ⚠", fontSize = 10.sp)
                }
                Text(if (row.debit > 0) fmt(row.debit) else "", Modifier.weight(0.25f), fontFamily = FigtreeFamily, fontSize = 10.sp, color = AppText)
                Text(if (row.credit > 0) fmt(row.credit) else "", Modifier.weight(0.25f), fontFamily = FigtreeFamily, fontSize = 10.sp, color = AppText)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (data.isBalanced) AppGreenSuccess.copy(alpha = 0.1f) else AppRedDestructive.copy(alpha = 0.1f),
                    RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            Text("TOTAL", Modifier.weight(0.5f), fontFamily = OutfitFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = AppText)
            Text(fmt(data.totalDebit), Modifier.weight(0.25f), fontFamily = OutfitFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = AppText)
            Text(fmt(data.totalCredit), Modifier.weight(0.25f), fontFamily = OutfitFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = AppText)
        }
        Text(
            text = if (data.isBalanced) "✓ Books are balanced" else "⚠ Out of balance by ${fmt(kotlin.math.abs(data.totalDebit - data.totalCredit))}",
            fontFamily = FigtreeFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            color = if (data.isBalanced) AppGreenSuccess else AppRedDestructive,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.height(16.dp))

        val topBalances = data.rows
            .map { it.name to (it.debit + it.credit) }
            .sortedByDescending { it.second }
            .take(8)
        if (topBalances.isNotEmpty()) {
            ChartCard("Top Balances") {
                HorizontalBarChart(data = topBalances, barColor = AppPrimary)
            }
        }
    }
}

// ─── 5. General Ledger ────────────────────────────────────────────────────

@Composable
fun GeneralLedgerReport(viewModel: ReportsViewModel, from: String, to: String) {
    val ledgerAccounts = remember { viewModel.computeGeneralLedger(from, to) }

    if (ledgerAccounts.isEmpty()) {
        Text("No transactions in this period", fontFamily = FigtreeFamily, color = AppMutedText)
        return
    }

    ledgerAccounts.forEach { account ->
        var expanded by remember { mutableStateOf(false) }
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AppCard)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${account.code} — ${account.name}",
                            fontFamily = OutfitFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = AppText
                        )
                        Text(
                            text = "Closing: ${fmt(account.closingBalance)}",
                            fontFamily = FigtreeFamily,
                            fontSize = 11.sp,
                            color = if (account.closingBalance >= 0) AppGreenSuccess else AppRedDestructive
                        )
                    }
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        null,
                        tint = AppMutedText
                    )
                }

                AnimatedVisibility(visible = expanded) {
                    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().background(AppPrimary.copy(alpha = 0.06f), RoundedCornerShape(6.dp)).padding(6.dp)
                        ) {
                            Text("Date", Modifier.weight(0.2f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
                            Text("Description", Modifier.weight(0.3f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
                            Text("Debit", Modifier.weight(0.17f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
                            Text("Credit", Modifier.weight(0.17f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
                            Text("Balance", Modifier.weight(0.16f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
                        }

                        account.rows.forEach { row ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp, horizontal = 4.dp)) {
                                Text(row.date, Modifier.weight(0.2f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppMutedText)
                                Text(row.desc.take(20), Modifier.weight(0.3f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppMutedText, maxLines = 1)
                                Text(if (row.debit > 0) fmt(row.debit) else "", Modifier.weight(0.17f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppGreenSuccess)
                                Text(if (row.credit > 0) fmt(row.credit) else "", Modifier.weight(0.17f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppRedDestructive)
                                Text(fmt(row.balance), Modifier.weight(0.16f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
                            }
                        }

                        if (account.rows.size > 1) {
                            val chartData = account.rows.map { it.date to it.balance }
                            ChartCard("Balance Trend") {
                                AreaChart(data = chartData, areaColor = AppAccentBlue)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

// ─── 6. Journal Report ────────────────────────────────────────────────────

@Composable
fun JournalReportView(viewModel: ReportsViewModel, from: String, to: String) {
    val entries = remember { viewModel.computeJournalReport(from, to) }

    if (entries.isEmpty()) {
        Text("No posted entries in this period", fontFamily = FigtreeFamily, color = AppMutedText)
        return
    }

    entries.forEach { entry ->
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AppCard)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = entry.description,
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = AppText,
                        modifier = Modifier.weight(1f)
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = entry.reference, fontFamily = FigtreeFamily, fontSize = 10.sp, color = AppPrimary)
                        Text(text = entry.date, fontFamily = FigtreeFamily, fontSize = 10.sp, color = AppMutedText)
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = AppBorder)
                entry.lines.forEach { line ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${line.accountCode} ${line.accountName}".trim(),
                            fontFamily = FigtreeFamily,
                            fontSize = 10.sp,
                            color = AppText,
                            modifier = Modifier.weight(1f)
                        )
                        if (line.debit > 0) {
                            Text(fmt(line.debit), fontFamily = FigtreeFamily, fontSize = 10.sp, color = AppGreenSuccess)
                        }
                        if (line.credit > 0) {
                            Text(fmt(line.credit), fontFamily = FigtreeFamily, fontSize = 10.sp, color = AppRedDestructive)
                        }
                    }
                }
            }
        }
    }
}

// ─── 7. Expense Analysis ──────────────────────────────────────────────────

@Composable
fun ExpenseAnalysisReport(viewModel: ReportsViewModel, from: String, to: String) {
    val data = remember { viewModel.computeExpenseAnalysis(from, to) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = AppPrimary.copy(alpha = 0.06f))
        ) {
            Text(
                text = "Total Revenue Reference: ${fmt(data.revenueTotal)}",
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = AppPrimary,
                modifier = Modifier.padding(10.dp)
            )
        }

        if (data.cogsRows.isNotEmpty()) {
            CollapsibleReportSection("Cost of Sales", fmt(data.cogsTotal)) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(AppPrimary.copy(alpha = 0.06f), RoundedCornerShape(6.dp)).padding(6.dp)
                ) {
                    Text("Account", Modifier.weight(0.4f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
                    Text("Amount", Modifier.weight(0.2f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
                    Text("% Rev", Modifier.weight(0.2f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
                    Text("% Total", Modifier.weight(0.2f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
                }
                data.cogsRows.forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp, horizontal = 4.dp)) {
                        Text(row.label, Modifier.weight(0.4f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppText)
                        Text(fmt(row.amount), Modifier.weight(0.2f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppText)
                        Text("${String.format("%.1f", row.pctRevenue)}%", Modifier.weight(0.2f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppMutedText)
                        Text("${String.format("%.1f", row.pctTotal)}%", Modifier.weight(0.2f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppMutedText)
                    }
                }
            }
        }

        CollapsibleReportSection("Operating Expenses", fmt(data.expenseTotal)) {
            Row(
                modifier = Modifier.fillMaxWidth().background(AppPrimary.copy(alpha = 0.06f), RoundedCornerShape(6.dp)).padding(6.dp)
            ) {
                Text("Account", Modifier.weight(0.4f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
                Text("Amount", Modifier.weight(0.2f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
                Text("% Rev", Modifier.weight(0.2f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
                Text("% Total", Modifier.weight(0.2f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
            }
            data.expenseRows.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp, horizontal = 4.dp)) {
                    Text(row.label, Modifier.weight(0.4f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppText)
                    Text(fmt(row.amount), Modifier.weight(0.2f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppText)
                    Text("${String.format("%.1f", row.pctRevenue)}%", Modifier.weight(0.2f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppMutedText)
                    Text("${String.format("%.1f", row.pctTotal)}%", Modifier.weight(0.2f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppMutedText)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            val expBars = data.expenseRows
                .sortedByDescending { it.amount }
                .take(8)
                .map { it.label to it.amount }
            if (expBars.isNotEmpty()) {
                ChartCard("Expense Breakdown", Modifier.weight(1f)) {
                    HorizontalBarChart(data = expBars, barColor = AppRedDestructive)
                }
            }

            val combined = (data.cogsRows.map { ReportsViewModel.ChartData(it.label, it.amount) } +
                    data.expenseRows.map { ReportsViewModel.ChartData(it.label, it.amount) })
                .sortedByDescending { it.value }.take(6)
            if (combined.isNotEmpty()) {
                Spacer(Modifier.width(12.dp))
                ChartCard("Total Cost Distribution", Modifier.weight(1f)) {
                    PieChart(data = combined)
                }
            }
        }
    }
}

// ─── 8. Revenue Analysis ──────────────────────────────────────────────────

@Composable
fun RevenueAnalysisReport(viewModel: ReportsViewModel, from: String, to: String) {
    val data = remember { viewModel.computeRevenueAnalysis(from, to) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard("Total Revenue", fmt(data.totalRevenue), Modifier.weight(1f))
            StatCard("Current Month", fmt(data.currentMonth), Modifier.weight(1f))
            StatCard(
                "MoM Growth",
                "${String.format("%.1f", data.momGrowth)}%",
                Modifier.weight(1f),
                isSuccess = data.momGrowth >= 0,
                isDestructive = data.momGrowth < 0
            )
            StatCard(
                "vs 12mo Avg",
                fmt(data.varianceVsAvg),
                Modifier.weight(1f),
                isSuccess = data.varianceVsAvg >= 0,
                isDestructive = data.varianceVsAvg < 0
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                ChartCard("12-Month Revenue Trend") {
                    LineChart(data = data.monthlyTrend, lineColor = AppGreenSuccess)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (data.bySource.isNotEmpty()) {
                    ChartCard("Revenue by Source") {
                        PieChart(data = data.bySource)
                    }
                }
            }
        }

        if (data.bySource.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            ChartCard("Revenue Sources") {
                val maxSource = data.bySource.maxOf { it.value }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    data.bySource.forEach { source ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = source.label,
                                fontFamily = FigtreeFamily,
                                fontSize = 10.sp,
                                color = AppText,
                                modifier = Modifier.width(120.dp),
                                maxLines = 1
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AppSecondaryBg)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(fraction = (source.value / maxSource).toFloat().coerceIn(0f, 1f))
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AppGreenSuccess)
                                )
                            }
                            Text(
                                text = "${((source.value / data.totalRevenue) * 100).toInt()}%",
                                fontFamily = FigtreeFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                color = AppMutedText,
                                modifier = Modifier.width(40.dp).padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── 9. A/P Aging ─────────────────────────────────────────────────────────

@Composable
fun APAgingReport(viewModel: ReportsViewModel, endDate: String) {
    AgingReportContent(
        title = "Accounts Payable Aging",
        data = remember { viewModel.computeAPAging(endDate) }
    )
}

// ─── 10. A/R Aging ────────────────────────────────────────────────────────

@Composable
fun ARAgingReport(viewModel: ReportsViewModel, endDate: String) {
    AgingReportContent(
        title = "Accounts Receivable Aging",
        data = remember { viewModel.computeARAging(endDate) }
    )
}

@Composable
private fun AgingReportContent(title: String, data: ReportsViewModel.AgingData) {
    if (data.totalOutstanding < 0.01) {
        Text("No outstanding items", fontFamily = FigtreeFamily, color = AppMutedText)
        return
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = AppAmberWarning.copy(alpha = 0.1f))
        ) {
            Text(
                text = "Total Outstanding: ${fmt(data.totalOutstanding)}",
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = AppAmberWarning,
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        ReportSection("Aging Buckets") {
            Row(
                modifier = Modifier.fillMaxWidth().background(AppPrimary.copy(alpha = 0.06f), RoundedCornerShape(6.dp)).padding(6.dp)
            ) {
                Text("Bucket", Modifier.weight(0.3f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
                Text("Amount", Modifier.weight(0.35f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
                Text("Count", Modifier.weight(0.35f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
            }
            data.buckets.forEach { bucket ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp, horizontal = 4.dp)) {
                    Text(bucket.label, Modifier.weight(0.3f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppText)
                    Text(fmt(bucket.amount), Modifier.weight(0.35f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppText)
                    Text("${bucket.count} items", Modifier.weight(0.35f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppMutedText)
                }
            }
        }

        if (data.details.isNotEmpty()) {
            ReportSection("Outstanding Detail") {
                data.details.forEach { detail ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                        Text(detail.date, Modifier.weight(0.25f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppMutedText)
                        Text(detail.description.take(18), Modifier.weight(0.45f), fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppText, maxLines = 1)
                        Text(fmt(detail.remaining), Modifier.weight(0.3f), fontFamily = FigtreeFamily, fontWeight = FontWeight.SemiBold, fontSize = 9.sp, color = AppText)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        val bucketBars = data.buckets.map { it.label to it.amount }
        ChartCard("Aging Distribution") {
            HorizontalBarChart(data = bucketBars, barColor = AppAmberWarning)
        }
    }
}

// ─── 11. Tax Summary ──────────────────────────────────────────────────────

@Composable
fun TaxSummaryReport(viewModel: ReportsViewModel, from: String, to: String) {
    val data = remember { viewModel.computeTaxSummary(from, to) }

    Column(modifier = Modifier.fillMaxWidth()) {
        ReportRow("Revenue", fmt(data.revenue))
        ReportRow("Less: COGS", "(${fmt(data.cogs)})")
        ReportRow("Gross Profit", fmt(data.grossProfit), isBold = true)
        ReportRow("Less: Operating Expenses", "(${fmt(data.expenses)})")
        ReportRow("Taxable Income", fmt(data.taxableIncome), isBold = true,
            isSuccess = data.taxableIncome >= 0, isDestructive = data.taxableIncome < 0)

        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = AppRedDestructive.copy(alpha = 0.08f))
        ) {
            ReportRow(
                label = "Estimated Tax (30%)",
                value = fmt(data.estimatedTax),
                isBold = true,
                isDestructive = true
            )
        }

        Spacer(Modifier.height(16.dp))

        ChartCard("Tax Computation") {
            BarChart(
                data = listOf(
                    "Revenue" to data.revenue,
                    "COGS" to data.cogs,
                    "Gross Profit" to data.grossProfit,
                    "Expenses" to data.expenses,
                    "Taxable" to data.taxableIncome,
                    "Tax (30%)" to data.estimatedTax
                ),
                barColor = Color(0xFF8B5CF6)
            )
        }
    }
}

// ─── Collapsible Section Helper ────────────────────────────────────────────

@Composable
fun CollapsibleReportSection(title: String, total: String, content: @Composable ColumnScope.() -> Unit) {
    var expanded by remember { mutableStateOf(true) }
    Column(modifier = Modifier.padding(bottom = 4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AppPrimary.copy(alpha = 0.04f))
                .clickable { expanded = !expanded }
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    null,
                    tint = AppMutedText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = title,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = AppText
                )
            }
            Text(
                text = total,
                fontFamily = FigtreeFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = AppPrimary
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(start = 4.dp, top = 2.dp)) {
                content()
            }
        }
    }
}
