package com.example.pesapilotandroid.ui.screens.money

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pesapilotandroid.data.model.UserBusiness
import com.example.pesapilotandroid.ui.components.LoadingScreen
import com.example.pesapilotandroid.ui.theme.*
import java.time.LocalDate

@Composable
fun ReportsScreen(viewModel: ReportsViewModel = hiltViewModel()) {
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
        BusinessSelector(
            businesses = uiState.businesses,
            selected = uiState.selectedBusiness,
            onSelect = { viewModel.selectBusiness(it) }
        )

        ReportCategoryCards(
            selectedGroup = uiState.selectedGroup,
            selectedReport = uiState.selectedReport,
            onSelectGroup = { viewModel.selectReportGroup(it) },
            onSelectReport = { viewModel.selectReport(it) }
        )

        DateRangeToolbar(
            startDate = uiState.startDate,
            endDate = uiState.endDate,
            onStartDateChange = { viewModel.setStartDate(it) },
            onEndDateChange = { viewModel.setEndDate(it) }
        )

        if (uiState.selectedBusiness == null) {
            EmptyState("Select a business to view reports")
        } else if (uiState.accounts.isEmpty()) {
            EmptyState("No accounts initialized. Go to Chart of Accounts to set up.")
        } else {
            ReportArea(
                reportType = uiState.selectedReport,
                startDate = uiState.startDate,
                endDate = uiState.endDate,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun BusinessSelector(
    businesses: List<UserBusiness>,
    selected: UserBusiness?,
    onSelect: (UserBusiness) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.Default.Business, null, tint = AppPrimary, modifier = Modifier.size(20.dp))
        Box(modifier = Modifier.weight(1f)) {
            OutlinedCard(
                onClick = { expanded = true },
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selected?.name ?: "Select business",
                        fontFamily = FigtreeFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = AppText
                    )
                    Icon(Icons.Default.ArrowDropDown, null, tint = AppMutedText)
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                businesses.forEach { biz ->
                    DropdownMenuItem(
                        text = { Text(biz.name, fontFamily = FigtreeFamily) },
                        onClick = { onSelect(biz); expanded = false },
                        leadingIcon = {
                            if (biz.id == selected?.id)
                                Icon(Icons.Default.Check, null, tint = AppPrimary)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportCategoryCards(
    selectedGroup: ReportGroup,
    selectedReport: ReportType,
    onSelectGroup: (ReportGroup) -> Unit,
    onSelectReport: (ReportType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReportGroup.entries.forEach { group ->
            val isActive = selectedGroup == group
            var reportExpanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (isActive) Modifier.border(2.dp, AppPrimary, RoundedCornerShape(12.dp))
                        else Modifier
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isActive) AppPrimary.copy(alpha = 0.06f) else AppCard
                ),
                onClick = { onSelectGroup(group) }
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (group) {
                            ReportGroup.FINANCIAL -> "FINANCIAL"
                            ReportGroup.CONTROL -> "CONTROL"
                            ReportGroup.MANAGEMENT -> "MGMT"
                            ReportGroup.CUSTOMER -> "CUST"
                            ReportGroup.TAX -> "TAX"
                        },
                        fontFamily = FigtreeFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = if (isActive) AppPrimary else AppMutedText
                    )

                    Box {
                        OutlinedCard(
                            onClick = { reportExpanded = true },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = selectedReport.takeIf { it.group == group }?.label?.take(14) ?: "Select",
                                fontFamily = FigtreeFamily,
                                fontSize = 10.sp,
                                color = AppText,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                        DropdownMenu(expanded = reportExpanded, onDismissRequest = { reportExpanded = false }) {
                            group.reports.forEach { report ->
                                DropdownMenuItem(
                                    text = { Text(report.label, fontSize = 11.sp) },
                                    onClick = {
                                        onSelectGroup(group)
                                        onSelectReport(report)
                                        reportExpanded = false
                                    }
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
private fun DateRangeToolbar(
    startDate: String,
    endDate: String,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedCard(
            onClick = {
                val date = try { LocalDate.parse(startDate) } catch (_: Exception) { LocalDate.now() }
                DatePickerDialog(context, { _, y, m, d ->
                    onStartDateChange(LocalDate.of(y, m + 1, d).toString())
                }, date.year, date.monthValue - 1, date.dayOfMonth).show()
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, null, tint = AppMutedText, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(text = startDate, fontFamily = FigtreeFamily, fontSize = 11.sp, color = AppText)
            }
        }

        Text("to", fontFamily = FigtreeFamily, fontSize = 11.sp, color = AppMutedText)

        OutlinedCard(
            onClick = {
                val date = try { LocalDate.parse(endDate) } catch (_: Exception) { LocalDate.now() }
                DatePickerDialog(context, { _, y, m, d ->
                    onEndDateChange(LocalDate.of(y, m + 1, d).toString())
                }, date.year, date.monthValue - 1, date.dayOfMonth).show()
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, null, tint = AppMutedText, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(text = endDate, fontFamily = FigtreeFamily, fontSize = 11.sp, color = AppText)
            }
        }
    }
}

@Composable
private fun ReportArea(
    reportType: ReportType,
    startDate: String,
    endDate: String,
    viewModel: ReportsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        ReportHeader(reportType.label, startDate, endDate)

        when (reportType) {
            ReportType.INCOME_STATEMENT -> IncomeStatementReport(viewModel, startDate, endDate)
            ReportType.BALANCE_SHEET -> BalanceSheetReport(viewModel, endDate, startDate, endDate)
            ReportType.CASH_FLOW -> CashFlowReport(viewModel, startDate, endDate)
            ReportType.TRIAL_BALANCE -> TrialBalanceReport(viewModel, endDate)
            ReportType.GENERAL_LEDGER -> GeneralLedgerReport(viewModel, startDate, endDate)
            ReportType.JOURNAL_REPORT -> JournalReportView(viewModel, startDate, endDate)
            ReportType.EXPENSE_ANALYSIS -> ExpenseAnalysisReport(viewModel, startDate, endDate)
            ReportType.REVENUE_ANALYSIS -> RevenueAnalysisReport(viewModel, startDate, endDate)
            ReportType.AP_AGING -> APAgingReport(viewModel, endDate)
            ReportType.AR_AGING -> ARAgingReport(viewModel, endDate)
            ReportType.TAX_SUMMARY -> TaxSummaryReport(viewModel, startDate, endDate)
            ReportType.SALES_BY_CUSTOMER -> SalesByCustomerReport(viewModel, startDate, endDate)
            ReportType.CUSTOMER_STATEMENT -> {
                CustomerSelector(uiState.contacts, uiState.selectedCustomerId) { viewModel.selectCustomer(it) }
                CustomerStatementReport(viewModel, uiState.selectedCustomerId, startDate, endDate)
            }
            ReportType.CUSTOMER_LEDGER -> {
                CustomerSelector(uiState.contacts, uiState.selectedCustomerId) { viewModel.selectCustomer(it) }
                CustomerLedgerReport(viewModel, uiState.selectedCustomerId)
            }
            ReportType.CUSTOMER_CREDIT -> CustomerCreditReport(viewModel, endDate)
            ReportType.SALES_REGISTER -> SalesRegisterReport(viewModel, startDate, endDate)
            ReportType.MONTHLY_SALES -> MonthlySalesReport(viewModel, startDate, endDate)
            ReportType.INVENTORY_VALUATION -> InventoryValuationReport(viewModel, startDate, endDate)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun CustomerSelector(
    contacts: List<com.example.pesapilotandroid.data.model.Contact>,
    selectedId: String?,
    onSelect: (String) -> Unit,
) {
    val customers = contacts.filter { it.type == "customer" }
    var expanded by remember { mutableStateOf(false) }
    val selectedName = customers.find { it.id == selectedId }?.name ?: "Select customer"
    Box(modifier = Modifier.padding(bottom = 8.dp)) {
        OutlinedCard(onClick = { expanded = true }, shape = RoundedCornerShape(8.dp)) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Default.Person, null, tint = AppMutedText, modifier = Modifier.size(16.dp))
                Text(selectedName, fontFamily = FigtreeFamily, fontSize = 12.sp, color = AppText)
                Icon(Icons.Default.ArrowDropDown, null, tint = AppMutedText)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            if (customers.isEmpty()) {
                DropdownMenuItem(text = { Text("No customers yet", fontSize = 11.sp) }, onClick = { expanded = false })
            }
            customers.forEach { c ->
                DropdownMenuItem(text = { Text(c.name, fontSize = 11.sp) }, onClick = { onSelect(c.id); expanded = false })
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Description, null, tint = AppMutedText, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(12.dp))
            Text(
                text = message,
                fontFamily = FigtreeFamily,
                fontSize = 13.sp,
                color = AppMutedText,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ReportHeader(title: String, from: String, to: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            fontFamily = OutfitFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = AppText
        )
        Text(
            text = "$from — $to",
            fontFamily = FigtreeFamily,
            fontSize = 12.sp,
            color = AppMutedText
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = AppBorder)
    }
}

@Composable
fun ReportSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = title,
            fontFamily = OutfitFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = AppMutedText,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        content()
    }
}

@Composable
fun ReportRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isSuccess: Boolean = false,
    isDestructive: Boolean = false,
    indent: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp, horizontal = if (indent) 16.dp else 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = if (isBold) OutfitFamily else FigtreeFamily,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (isBold) 12.sp else 11.sp,
            color = AppText
        )
        Text(
            text = value,
            fontFamily = FigtreeFamily,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (isBold) 12.sp else 11.sp,
            color = when {
                isSuccess -> AppGreenSuccess
                isDestructive -> AppRedDestructive
                else -> AppText
            }
        )
    }
    HorizontalDivider(color = AppBorder.copy(alpha = 0.3f))
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isSuccess: Boolean = false,
    isDestructive: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = AppCard)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontFamily = FigtreeFamily, fontSize = 9.sp, color = AppMutedText)
            Text(
                text = value,
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = when {
                    isSuccess -> AppGreenSuccess
                    isDestructive -> AppRedDestructive
                    else -> AppText
                }
            )
        }
    }
}

@Composable
fun ChartCard(title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier.padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppCard)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = AppText,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}

fun fmt(v: Double): String = String.format(java.util.Locale.US, "%,.2f", v)
