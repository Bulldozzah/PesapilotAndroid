package com.example.pesapilotandroid.ui.screens.money

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesapilotandroid.data.model.*
import com.example.pesapilotandroid.data.repository.AccountingRepository
import com.example.pesapilotandroid.data.repository.AuthRepository
import com.example.pesapilotandroid.data.repository.BusinessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountingRepository: AccountingRepository,
    private val businessRepository: BusinessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        val today = LocalDate.now()
        _uiState.update {
            it.copy(
                startDate = today.withDayOfMonth(1).toString(),
                endDate = today.toString()
            )
        }
        loadBusinesses()
    }

    private fun loadBusinesses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = authRepository.getCurrentUserId() ?: return@launch
            businessRepository.getUserBusinesses(userId).onSuccess { businesses ->
                _uiState.update { it.copy(businesses = businesses, isLoading = false) }
                if (businesses.isNotEmpty()) selectBusiness(businesses.first())
            }.onFailure { _uiState.update { it.copy(isLoading = false) } }
        }
    }

    fun selectBusiness(business: UserBusiness) {
        _uiState.update { it.copy(selectedBusiness = business) }
        loadReportData(business.id)
    }

    private fun loadReportData(businessId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = authRepository.getCurrentUserId() ?: return@launch

            accountingRepository.getChartOfAccounts(userId, businessId).onSuccess { accounts ->
                _uiState.update { it.copy(accounts = accounts.sortedBy { a -> a.code }) }
            }
            accountingRepository.getContacts(userId, businessId).onSuccess { contacts ->
                _uiState.update { it.copy(contacts = contacts) }
            }
            accountingRepository.getJournalEntries(userId, businessId).onSuccess { entries ->
                val posted = entries.filter { it.isPosted }.sortedBy { it.entryDate }
                _uiState.update { it.copy(entries = posted) }
                if (posted.isNotEmpty()) {
                    accountingRepository.getLinesForEntries(posted.map { it.id }).onSuccess { lines ->
                        _uiState.update { it.copy(entryLines = lines) }
                    }
                }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun setStartDate(date: String) = _uiState.update { it.copy(startDate = date) }
    fun setEndDate(date: String) = _uiState.update { it.copy(endDate = date) }
    fun selectReportGroup(group: ReportGroup) = _uiState.update { it.copy(selectedGroup = group) }
    fun selectReport(report: ReportType) = _uiState.update { it.copy(selectedReport = report) }

    // ─── Core calculation functions ───────────────────────────────────────

    data class BalanceResult(val debit: Double, val credit: Double, val balance: Double)

    fun balanceUpTo(accountId: String, upToDate: String? = null): BalanceResult {
        var debit = 0.0; var credit = 0.0
        for (entry in _uiState.value.entries) {
            if (upToDate != null && entry.entryDate > upToDate) continue
            for (line in _uiState.value.entryLines) {
                if (line.journalEntryId == entry.id && line.accountId == accountId) {
                    debit += line.debit; credit += line.credit
                }
            }
        }
        return BalanceResult(debit, credit, debit - credit)
    }

    fun balanceInRange(accountId: String, fromDate: String, toDate: String): BalanceResult {
        var debit = 0.0; var credit = 0.0
        for (entry in _uiState.value.entries) {
            if (entry.entryDate < fromDate) continue
            if (entry.entryDate > toDate) break
            for (line in _uiState.value.entryLines) {
                if (line.journalEntryId == entry.id && line.accountId == accountId) {
                    debit += line.debit; credit += line.credit
                }
            }
        }
        return BalanceResult(debit, credit, debit - credit)
    }

    fun getAccountName(id: String): String {
        val acct = _uiState.value.accounts.find { it.id == id } ?: return id
        return "${acct.code} ${acct.name}"
    }

    private fun dayBefore(date: String): String = LocalDate.parse(date).minusDays(1).toString()

    private fun isOperatingIncome(a: ChartOfAccount) =
        a.type == "income" || a.accountType.equals("Revenue", true)

    /**
     * Signed net income from a per-account balance function (balance = debit − credit).
     * Credit-normal income contributes −balance so contra-revenue (a debit balance, e.g.
     * Sales Returns) correctly REDUCES income instead of being added via abs(); debit-normal
     * expense/COGS contribute +balance. Includes other income/expense.
     */
    private fun netIncomeFor(bal: (String) -> Double): Double {
        val accts = _uiState.value.accounts
        val rev = accts.filter { isOperatingIncome(it) }.sumOf { -bal(it.id) }
        val cogs = accts.filter { it.type == "cogs" }.sumOf { bal(it.id) }
        val exp = accts.filter { it.type == "expense" }.sumOf { bal(it.id) }
        val oInc = accts.filter { it.type == "other_income" }.sumOf { -bal(it.id) }
        val oExp = accts.filter { it.type == "other_expense" }.sumOf { bal(it.id) }
        return (rev - cogs - exp) + oInc - oExp
    }

    // ─── Report data classes ──────────────────────────────────────────────

    data class ReportRow(val label: String, val value: Double, val isBold: Boolean = false,
                         val isSuccess: Boolean = false, val isDestructive: Boolean = false,
                         val indent: Boolean = false)

    data class ChartData(val label: String, val value: Double, val color: Long = 0xFF4C7FBD)

    data class PnLData(val revenueRows: List<ReportRow>, val cogsRows: List<ReportRow>,
                       val expenseRows: List<ReportRow>,
                       val otherIncomeRows: List<ReportRow>, val otherExpenseRows: List<ReportRow>,
                       val grossProfit: Double, val operatingIncome: Double,
                       val netIncome: Double, val revenueTotal: Double,
                       val cogsTotal: Double, val expenseTotal: Double,
                       val otherIncomeTotal: Double, val otherExpenseTotal: Double)

    data class BalanceSheetData(val assetRows: List<ReportRow>, val liabilityRows: List<ReportRow>,
                                val equityRows: List<ReportRow>, val totalAssets: Double,
                                val totalLiabilities: Double, val totalEquity: Double,
                                val netIncome: Double, val isBalanced: Boolean)

    data class CashFlowData(val openingCash: Double, val closingCash: Double,
                            val operating: Double, val investing: Double, val financing: Double,
                            val operatingInflows: List<Pair<String, Double>>,
                            val operatingOutflows: List<Pair<String, Double>>,
                            val investingInflows: List<Pair<String, Double>>,
                            val investingOutflows: List<Pair<String, Double>>,
                            val financingInflows: List<Pair<String, Double>>,
                            val financingOutflows: List<Pair<String, Double>>)

    data class TrialBalanceRow(val code: String, val name: String, val debit: Double,
                               val credit: Double, val isAbnormal: Boolean)

    data class TrialBalanceData(val rows: List<TrialBalanceRow>, val totalDebit: Double,
                                val totalCredit: Double, val isBalanced: Boolean)

    data class LedgerRow(val date: String, val desc: String, val debit: Double,
                         val credit: Double, val balance: Double)

    data class LedgerAccount(val accountId: String, val code: String, val name: String,
                             val rows: List<LedgerRow>, val closingBalance: Double)

    data class JournalEntryRow(val accountCode: String, val accountName: String,
                               val debit: Double, val credit: Double)

    data class JournalReportEntry(val description: String, val reference: String,
                                  val date: String, val lines: List<JournalEntryRow>)

    data class ExpenseRow(val label: String, val amount: Double, val pctRevenue: Double,
                          val pctTotal: Double)

    data class ExpenseData(val revenueTotal: Double, val cogsRows: List<ExpenseRow>,
                           val expenseRows: List<ExpenseRow>, val cogsTotal: Double,
                           val expenseTotal: Double)

    data class RevenueData(val totalRevenue: Double, val currentMonth: Double,
                           val prevMonth: Double, val momGrowth: Double,
                           val avg12Month: Double, val varianceVsAvg: Double,
                           val monthlyTrend: List<Pair<String, Double>>,
                           val bySource: List<ChartData>)

    data class AgingBucket(val label: String, val amount: Double, val count: Int)

    data class AgingDetail(val date: String, val description: String, val remaining: Double)

    data class AgingData(val totalOutstanding: Double, val buckets: List<AgingBucket>,
                         val details: List<AgingDetail>)

    data class TaxData(val revenue: Double, val cogs: Double, val expenses: Double,
                       val grossProfit: Double, val taxableIncome: Double,
                       val estimatedTax: Double)

    // ─── Report computation functions ─────────────────────────────────────

    fun computePnL(from: String, to: String): PnLData {
        val revenueAccounts = _uiState.value.accounts.filter {
            it.type == "income" || it.accountType.equals("Revenue", true)
        }
        val cogsAccounts = _uiState.value.accounts.filter { it.type == "cogs" }
        val expenseAccounts = _uiState.value.accounts.filter { it.type == "expense" }
        val otherIncomeAccounts = _uiState.value.accounts.filter { it.type == "other_income" }
        val otherExpenseAccounts = _uiState.value.accounts.filter { it.type == "other_expense" }

        // Signed values: credit-normal income shown as −balance (contra-revenue appears
        // negative and nets the total down); debit-normal expense/COGS shown as +balance.
        // Rows with a zero balance are dropped.
        val revenueRows = revenueAccounts.map { acct ->
            ReportRow("${acct.code} ${acct.name}", -balanceInRange(acct.id, from, to).balance)
        }.filter { it.value != 0.0 }
        val cogsRows = cogsAccounts.map { acct ->
            ReportRow("${acct.code} ${acct.name}", balanceInRange(acct.id, from, to).balance)
        }.filter { it.value != 0.0 }
        val expenseRows = expenseAccounts.map { acct ->
            ReportRow("${acct.code} ${acct.name}", balanceInRange(acct.id, from, to).balance)
        }.filter { it.value != 0.0 }
        val otherIncomeRows = otherIncomeAccounts.map { acct ->
            ReportRow("${acct.code} ${acct.name}", -balanceInRange(acct.id, from, to).balance)
        }.filter { it.value != 0.0 }
        val otherExpenseRows = otherExpenseAccounts.map { acct ->
            ReportRow("${acct.code} ${acct.name}", balanceInRange(acct.id, from, to).balance)
        }.filter { it.value != 0.0 }

        val revTotal = revenueRows.sumOf { it.value }
        val cogsTotal = cogsRows.sumOf { it.value }
        val expTotal = expenseRows.sumOf { it.value }
        val otherIncTotal = otherIncomeRows.sumOf { it.value }
        val otherExpTotal = otherExpenseRows.sumOf { it.value }
        val grossProfit = revTotal - cogsTotal
        val operatingIncome = grossProfit - expTotal
        val netIncome = operatingIncome + otherIncTotal - otherExpTotal

        return PnLData(revenueRows, cogsRows, expenseRows, otherIncomeRows, otherExpenseRows,
            grossProfit, operatingIncome, netIncome,
            revTotal, cogsTotal, expTotal, otherIncTotal, otherExpTotal)
    }

    fun computeBalanceSheet(endDate: String, from: String, to: String): BalanceSheetData {
        val assets = _uiState.value.accounts.filter { it.type == "asset" }
        val liabilities = _uiState.value.accounts.filter { it.type == "liability" }
        val equity = _uiState.value.accounts.filter { it.type == "equity" }

        val assetRows = assets.map { acct ->
            ReportRow("${acct.code} ${acct.name}", balanceUpTo(acct.id, endDate).balance)
        }
        val liabilityRows = liabilities.map { acct ->
            ReportRow("${acct.code} ${acct.name}", -balanceUpTo(acct.id, endDate).balance)
        }
        val equityAccountRows = equity.map { acct ->
            ReportRow("${acct.code} ${acct.name}", -balanceUpTo(acct.id, endDate).balance)
        }

        val totalAssets = assetRows.sumOf { it.value }
        val totalLiabilities = liabilityRows.sumOf { it.value }
        val equitySum = equityAccountRows.sumOf { it.value }

        val pnl = computePnL(from, to)
        // Income/expense accounts are never closed to equity, so prior-period profit must be
        // added back as retained earnings or the sheet won't balance for any period that
        // doesn't start at inception. priorRetained = net income from inception to day-before-start.
        val priorRetained = netIncomeFor { balanceUpTo(it, dayBefore(from)).balance }
        val equityRows = if (kotlin.math.abs(priorRetained) > 0.005) {
            equityAccountRows + ReportRow("Retained Earnings (prior periods)", priorRetained)
        } else equityAccountRows
        val totalEquity = equitySum + priorRetained + pnl.netIncome
        val isBalanced = kotlin.math.abs(totalAssets - (totalLiabilities + totalEquity)) < 0.01

        return BalanceSheetData(assetRows, liabilityRows, equityRows, totalAssets, totalLiabilities, totalEquity, pnl.netIncome, isBalanced)
    }

    fun computeCashFlow(from: String, to: String): CashFlowData {
        val cashAccounts = _uiState.value.accounts.filter { acct ->
            acct.type == "asset" && (
                acct.name.matches(Regex(".*(cash|bank|checking|savings|wallet).*", RegexOption.IGNORE_CASE)) ||
                acct.code.matches(Regex("^10\\d{2}$"))
            )
        }
        val cashIds = cashAccounts.map { it.id }.toSet()

        val dayBefore = java.time.LocalDate.parse(from).minusDays(1).toString()
        val opening = cashAccounts.sumOf { balanceUpTo(it.id, dayBefore).balance }
        var operating = 0.0; var investing = 0.0; var financing = 0.0
        val opIn = mutableListOf<Pair<String, Double>>()
        val opOut = mutableListOf<Pair<String, Double>>()
        val invIn = mutableListOf<Pair<String, Double>>()
        val invOut = mutableListOf<Pair<String, Double>>()
        val finIn = mutableListOf<Pair<String, Double>>()
        val finOut = mutableListOf<Pair<String, Double>>()

        for (entry in _uiState.value.entries) {
            if (entry.entryDate < from || entry.entryDate > to) continue
            val lines = _uiState.value.entryLines.filter { it.journalEntryId == entry.id }
            val cashLines = lines.filter { it.accountId in cashIds }
            val nonCashLines = lines.filter { it.accountId !in cashIds }
            if (cashLines.isEmpty() || nonCashLines.isEmpty()) continue

            val net = cashLines.sumOf { it.debit - it.credit }
            if (net == 0.0) continue

            val nonCashAcct = _uiState.value.accounts.find { it.id == nonCashLines.first().accountId }
            val ncName = nonCashAcct?.name ?: "Other"
            val ncType = nonCashAcct?.type ?: ""
            val ncCode = nonCashAcct?.code ?: ""

            val bucket: Int = when {
                ncType == "equity" || ncName.matches(Regex(".*(loan|capital|dividend).*", RegexOption.IGNORE_CASE)) -> 2
                ncName.matches(Regex(".*(ppe|equipment|property|machinery|vehicle|building|furniture|fixture|land|intangible).*", RegexOption.IGNORE_CASE)) ||
                    (ncType == "asset" && ncCode.matches(Regex("^1[5-6]\\d{2}$"))) -> 1
                else -> 0
            }

            when (bucket) {
                0 -> { operating += net; if (net > 0) opIn.add(ncName to net) else opOut.add(ncName to kotlin.math.abs(net)) }
                1 -> { investing += net; if (net > 0) invIn.add(ncName to net) else invOut.add(ncName to kotlin.math.abs(net)) }
                2 -> { financing += net; if (net > 0) finIn.add(ncName to net) else finOut.add(ncName to kotlin.math.abs(net)) }
            }
        }

        val closing = opening + operating + investing + financing
        return CashFlowData(opening, closing, operating, investing, financing, opIn, opOut, invIn, invOut, finIn, finOut)
    }

    fun computeTrialBalance(endDate: String): TrialBalanceData {
        val creditNormal = setOf("liability", "equity", "income", "revenue", "other_income")
        val rows = _uiState.value.accounts.mapNotNull { acct ->
            val b = balanceUpTo(acct.id, endDate)
            if (kotlin.math.abs(b.balance) < 0.005) return@mapNotNull null
            val debit = if (b.balance > 0) b.balance else 0.0
            val credit = if (b.balance < 0) -b.balance else 0.0
            val isAbnormal = if (acct.type in creditNormal) debit > 0 else credit > 0
            TrialBalanceRow(acct.code, acct.name, debit, credit, isAbnormal)
        }.sortedBy { it.code }

        val totalDebit = rows.sumOf { it.debit }
        val totalCredit = rows.sumOf { it.credit }
        return TrialBalanceData(rows, totalDebit, totalCredit, kotlin.math.abs(totalDebit - totalCredit) < 0.01)
    }

    fun computeGeneralLedger(from: String, to: String): List<LedgerAccount> {
        val result = mutableListOf<LedgerAccount>()
        for (acct in _uiState.value.accounts) {
            // Carry the balance from before the period forward so the running balance is the
            // true account balance, not just in-period movement.
            val opening = balanceUpTo(acct.id, dayBefore(from)).balance
            var running = opening
            val rows = mutableListOf<LedgerRow>()
            for (entry in _uiState.value.entries) {
                if (entry.entryDate < from || entry.entryDate > to) continue
                for (line in _uiState.value.entryLines) {
                    if (line.journalEntryId == entry.id && line.accountId == acct.id) {
                        running += line.debit - line.credit
                        rows.add(LedgerRow(entry.entryDate, entry.description ?: "", line.debit, line.credit, running))
                    }
                }
            }
            if (rows.isNotEmpty() || kotlin.math.abs(opening) > 0.005) {
                val withOpening = listOf(LedgerRow(from, "Opening balance", 0.0, 0.0, opening)) + rows
                result.add(LedgerAccount(acct.id, acct.code, acct.name, withOpening, running))
            }
        }
        return result
    }

    fun computeJournalReport(from: String, to: String): List<JournalReportEntry> {
        return _uiState.value.entries.filter { it.entryDate >= from && it.entryDate <= to }.map { entry ->
            val lines = _uiState.value.entryLines.filter { it.journalEntryId == entry.id }.map { line ->
                val acct = _uiState.value.accounts.find { it.id == line.accountId }
                JournalEntryRow(acct?.code ?: "", acct?.name ?: line.accountId, line.debit, line.credit)
            }
            JournalReportEntry(entry.description ?: "", entry.reference ?: "", entry.entryDate, lines)
        }
    }

    fun computeExpenseAnalysis(from: String, to: String): ExpenseData {
        // Operating revenue, signed (contra-revenue nets down the reference total).
        val revenueTotal = _uiState.value.accounts.filter { isOperatingIncome(it) }
            .sumOf { -balanceInRange(it.id, from, to).balance }

        val cogsAccounts = _uiState.value.accounts.filter { it.type == "cogs" }
        val expenseAccounts = _uiState.value.accounts.filter { it.type == "expense" }

        val cogsRows = cogsAccounts.map { acct ->
            val amt = balanceInRange(acct.id, from, to).balance
            ExpenseRow("${acct.code} ${acct.name}", amt, if (revenueTotal > 0) (amt / revenueTotal) * 100 else 0.0, 0.0)
        }.filter { it.amount != 0.0 }
        val cogsTotal = cogsRows.sumOf { it.amount }

        val expRows = expenseAccounts.map { acct ->
            val amt = balanceInRange(acct.id, from, to).balance
            ExpenseRow("${acct.code} ${acct.name}", amt, if (revenueTotal > 0) (amt / revenueTotal) * 100 else 0.0, 0.0)
        }.filter { it.amount != 0.0 }
        val expTotal = expRows.sumOf { it.amount }

        val allExpRows = expRows.map { r ->
            r.copy(pctTotal = if (expTotal > 0) (r.amount / expTotal) * 100 else 0.0)
        }
        return ExpenseData(revenueTotal, cogsRows, allExpRows, cogsTotal, expTotal)
    }

    fun computeRevenueAnalysis(from: String, to: String): RevenueData {
        // Operating revenue only, signed so contra-revenue (returns/discounts) nets down.
        val incomeAccounts = _uiState.value.accounts.filter { isOperatingIncome(it) }
        val totalRevenue = incomeAccounts.sumOf { -balanceInRange(it.id, from, to).balance }

        val monthlyTrend = mutableListOf<Pair<String, Double>>()
        val endDate = LocalDate.parse(to)
        for (i in 11 downTo 0) {
            val monthStart = endDate.minusMonths(i.toLong()).withDayOfMonth(1)
            val monthEnd = monthStart.plusMonths(1).minusDays(1)
            val mFrom = monthStart.toString()
            val mTo = if (i == 0) to else monthEnd.toString()
            val mRev = incomeAccounts.sumOf { -balanceInRange(it.id, mFrom, mTo).balance }
            monthlyTrend.add("${monthStart.year}-${monthStart.monthValue.toString().padStart(2, '0')}" to mRev)
        }

        val currentMonth = monthlyTrend.lastOrNull()?.second ?: 0.0
        val prevMonth = monthlyTrend.getOrNull(monthlyTrend.size - 2)?.second ?: 0.0
        val momGrowth = if (prevMonth > 0) ((currentMonth - prevMonth) / prevMonth) * 100 else 0.0
        val avg12 = if (monthlyTrend.isNotEmpty()) monthlyTrend.sumOf { it.second } / monthlyTrend.size else 0.0

        val bySource = incomeAccounts.map { acct ->
            ChartData("${acct.code} ${acct.name}", -balanceInRange(acct.id, from, to).balance)
        }.filter { it.value > 0 }.sortedByDescending { it.value }

        return RevenueData(totalRevenue, currentMonth, prevMonth, momGrowth, avg12, currentMonth - avg12, monthlyTrend, bySource)
    }

    fun computeAPAging(endDate: String): AgingData {
        val apAccount = _uiState.value.accounts.find {
            it.name.matches(Regex(".*accounts?\\s*payable|\\bpayable\\b.*", RegexOption.IGNORE_CASE))
        } ?: return AgingData(0.0, emptyList(), emptyList())

        data class Charge(val date: String, val desc: String, var remaining: Double)
        // Group charges and payments by vendor so one vendor's payment can't settle another's
        // invoice. Lines without a vendor id fall into a shared "unattributed" bucket.
        val chargesByContact = mutableMapOf<String, MutableList<Charge>>()
        val poolByContact = mutableMapOf<String, Double>()

        for (entry in _uiState.value.entries) {
            if (entry.entryDate > endDate) break
            for (line in _uiState.value.entryLines) {
                if (line.journalEntryId == entry.id && line.accountId == apAccount.id) {
                    val contact = line.vendorId ?: "unattributed"
                    if (line.credit > 0) {
                        chargesByContact.getOrPut(contact) { mutableListOf() }
                            .add(Charge(entry.entryDate, entry.description ?: "", line.credit))
                    }
                    if (line.debit > 0) poolByContact[contact] = (poolByContact[contact] ?: 0.0) + line.debit
                }
            }
        }

        // FIFO settlement within each contact (oldest charges first).
        val charges = mutableListOf<Charge>()
        for ((contact, list) in chargesByContact) {
            list.sortBy { it.date }
            var pool = poolByContact[contact] ?: 0.0
            for (charge in list) {
                if (pool <= 0) break
                val apply = minOf(charge.remaining, pool)
                charge.remaining -= apply
                pool -= apply
            }
            charges.addAll(list)
        }
        charges.sortBy { it.date }

        val outstanding = charges.filter { it.remaining > 0.01 }
        val refDate = LocalDate.parse(endDate)
        val buckets = mutableMapOf("0-30" to 0.0, "31-60" to 0.0, "61-90" to 0.0, "91-120" to 0.0, "120+" to 0.0)
        val counts = mutableMapOf("0-30" to 0, "31-60" to 0, "61-90" to 0, "91-120" to 0, "120+" to 0)

        for (c in outstanding) {
            val days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.parse(c.date), refDate)
            val bucket = when {
                days <= 30 -> "0-30"
                days <= 60 -> "31-60"
                days <= 90 -> "61-90"
                days <= 120 -> "91-120"
                else -> "120+"
            }
            buckets[bucket] = (buckets[bucket] ?: 0.0) + c.remaining
            counts[bucket] = (counts[bucket] ?: 0) + 1
        }

        val bucketList = listOf("0-30", "31-60", "61-90", "91-120", "120+").map {
            AgingBucket(it, buckets[it] ?: 0.0, counts[it] ?: 0)
        }
        val details = outstanding.map { AgingDetail(it.date, it.desc, it.remaining) }
        return AgingData(outstanding.sumOf { it.remaining }, bucketList, details)
    }

    fun computeARAging(endDate: String): AgingData {
        val arAccount = _uiState.value.accounts.find {
            it.name.matches(Regex(".*accounts?\\s*receivable|\\breceivable\\b.*", RegexOption.IGNORE_CASE))
        } ?: return AgingData(0.0, emptyList(), emptyList())

        data class Charge(val date: String, val desc: String, var remaining: Double)
        // Group invoices and collections by customer so one customer's payment can't settle
        // another's invoice. Lines without a customer id fall into a shared "unattributed" bucket.
        val chargesByContact = mutableMapOf<String, MutableList<Charge>>()
        val poolByContact = mutableMapOf<String, Double>()

        for (entry in _uiState.value.entries) {
            if (entry.entryDate > endDate) break
            for (line in _uiState.value.entryLines) {
                if (line.journalEntryId == entry.id && line.accountId == arAccount.id) {
                    val contact = line.customerId ?: "unattributed"
                    if (line.debit > 0) {
                        chargesByContact.getOrPut(contact) { mutableListOf() }
                            .add(Charge(entry.entryDate, entry.description ?: "", line.debit))
                    }
                    if (line.credit > 0) poolByContact[contact] = (poolByContact[contact] ?: 0.0) + line.credit
                }
            }
        }

        val charges = mutableListOf<Charge>()
        for ((contact, list) in chargesByContact) {
            list.sortBy { it.date }
            var pool = poolByContact[contact] ?: 0.0
            for (charge in list) {
                if (pool <= 0) break
                val apply = minOf(charge.remaining, pool)
                charge.remaining -= apply
                pool -= apply
            }
            charges.addAll(list)
        }
        charges.sortBy { it.date }

        val outstanding = charges.filter { it.remaining > 0.01 }
        val refDate = LocalDate.parse(endDate)
        val buckets = mutableMapOf("0-30" to 0.0, "31-60" to 0.0, "61-90" to 0.0, "91-120" to 0.0, "120+" to 0.0)
        val counts = mutableMapOf("0-30" to 0, "31-60" to 0, "61-90" to 0, "91-120" to 0, "120+" to 0)

        for (c in outstanding) {
            val days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.parse(c.date), refDate)
            val bucket = when {
                days <= 30 -> "0-30"
                days <= 60 -> "31-60"
                days <= 90 -> "61-90"
                days <= 120 -> "91-120"
                else -> "120+"
            }
            buckets[bucket] = (buckets[bucket] ?: 0.0) + c.remaining
            counts[bucket] = (counts[bucket] ?: 0) + 1
        }

        val bucketList = listOf("0-30", "31-60", "61-90", "91-120", "120+").map {
            AgingBucket(it, buckets[it] ?: 0.0, counts[it] ?: 0)
        }
        val details = outstanding.map { AgingDetail(it.date, it.desc, it.remaining) }
        return AgingData(outstanding.sumOf { it.remaining }, bucketList, details)
    }

    fun computeTaxSummary(from: String, to: String): TaxData {
        // Reuse the corrected, signed P&L so taxable income = full accounting net income
        // (nets contra accounts and includes other income/expense), consistent with the P&L.
        val pnl = computePnL(from, to)
        val revenue = pnl.revenueTotal
        val cogs = pnl.cogsTotal
        val expenses = pnl.expenseTotal
        val grossProfit = pnl.grossProfit
        val taxableIncome = pnl.netIncome
        val estimatedTax = if (taxableIncome > 0) taxableIncome * 0.30 else 0.0

        return TaxData(revenue, cogs, expenses, grossProfit, taxableIncome, estimatedTax)
    }
}

enum class ReportGroup(val label: String, val reports: List<ReportType>) {
    FINANCIAL("Financial Statements", listOf(ReportType.INCOME_STATEMENT, ReportType.BALANCE_SHEET, ReportType.CASH_FLOW)),
    CONTROL("Control Reports", listOf(ReportType.TRIAL_BALANCE, ReportType.GENERAL_LEDGER, ReportType.JOURNAL_REPORT)),
    MANAGEMENT("Management Reports", listOf(ReportType.EXPENSE_ANALYSIS, ReportType.REVENUE_ANALYSIS, ReportType.AP_AGING, ReportType.AR_AGING)),
    TAX("Tax & Compliance", listOf(ReportType.TAX_SUMMARY))
}

enum class ReportType(val label: String, val group: ReportGroup) {
    INCOME_STATEMENT("Income Statement", ReportGroup.FINANCIAL),
    BALANCE_SHEET("Balance Sheet", ReportGroup.FINANCIAL),
    CASH_FLOW("Cash Flow", ReportGroup.FINANCIAL),
    TRIAL_BALANCE("Trial Balance", ReportGroup.CONTROL),
    GENERAL_LEDGER("General Ledger", ReportGroup.CONTROL),
    JOURNAL_REPORT("Journal Report", ReportGroup.CONTROL),
    EXPENSE_ANALYSIS("Expense Analysis", ReportGroup.MANAGEMENT),
    REVENUE_ANALYSIS("Revenue Analysis", ReportGroup.MANAGEMENT),
    AP_AGING("A/P Aging", ReportGroup.MANAGEMENT),
    AR_AGING("A/R Aging", ReportGroup.MANAGEMENT),
    TAX_SUMMARY("Tax Summary", ReportGroup.TAX)
}

data class ReportsUiState(
    val isLoading: Boolean = false,
    val businesses: List<UserBusiness> = emptyList(),
    val selectedBusiness: UserBusiness? = null,
    val accounts: List<ChartOfAccount> = emptyList(),
    val entries: List<JournalEntry> = emptyList(),
    val entryLines: List<JournalEntryLine> = emptyList(),
    val contacts: List<Contact> = emptyList(),
    val startDate: String = "",
    val endDate: String = "",
    val selectedGroup: ReportGroup = ReportGroup.FINANCIAL,
    val selectedReport: ReportType = ReportType.INCOME_STATEMENT
)
