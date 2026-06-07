package com.example.pesapilotandroid.ui.screens.money

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pesapilotandroid.ui.components.PesaPilotTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(
    reportType: String,
    navController: NavController
) {
    val reportTitle = when (reportType) {
        "income_statement" -> "Income Statement"
        "balance_sheet" -> "Balance Sheet"
        "cash_flow" -> "Cash Flow Statement"
        "trial_balance" -> "Trial Balance"
        "general_ledger" -> "General Ledger"
        "expense_analysis" -> "Expense Analysis"
        "revenue_analysis" -> "Revenue Analysis"
        else -> "Report"
    }

    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = reportTitle,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Date range selector placeholder
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Date Range",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "This Month",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            when (reportType) {
                "income_statement" -> {
                    item {
                        ReportSection(title = "Income", items = listOf(
                            "Sales Revenue" to "$0.00",
                            "Service Revenue" to "$0.00",
                            "Other Income" to "$0.00"
                        ), total = "Total Income" to "$0.00")
                    }
                    item {
                        ReportSection(title = "Expenses", items = listOf(
                            "Cost of Goods Sold" to "$0.00",
                            "Operating Expenses" to "$0.00",
                            "Administrative Expenses" to "$0.00"
                        ), total = "Total Expenses" to "$0.00")
                    }
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Net Profit/Loss",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "$0.00",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                "balance_sheet" -> {
                    item {
                        ReportSection(title = "Assets", items = listOf(
                            "Cash & Bank" to "$0.00",
                            "Accounts Receivable" to "$0.00",
                            "Inventory" to "$0.00",
                            "Fixed Assets" to "$0.00"
                        ), total = "Total Assets" to "$0.00")
                    }
                    item {
                        ReportSection(title = "Liabilities", items = listOf(
                            "Accounts Payable" to "$0.00",
                            "Loans Payable" to "$0.00",
                            "Other Liabilities" to "$0.00"
                        ), total = "Total Liabilities" to "$0.00")
                    }
                    item {
                        ReportSection(title = "Equity", items = listOf(
                            "Owner's Capital" to "$0.00",
                            "Retained Earnings" to "$0.00"
                        ), total = "Total Equity" to "$0.00")
                    }
                }
                else -> {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No data available.\nCreate journal entries to generate reports.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportSection(
    title: String,
    items: List<Pair<String, String>>,
    total: Pair<String, String>
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            items.forEach { (name, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = total.first,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = total.second,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
