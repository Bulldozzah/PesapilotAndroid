package com.example.pesapilotandroid.data.repository

import com.example.pesapilotandroid.data.model.AccountSubcategory
import com.example.pesapilotandroid.data.model.ChartOfAccount
import java.util.UUID

fun getDefaultSubcategories(userId: String, businessId: String): List<AccountSubcategory> = listOf(
    AccountSubcategory(id = UUID.randomUUID().toString(), userBusinessId = businessId, userId = userId, accountType = "asset", name = "Current Assets", displayOrder = 1, isSystem = true),
    AccountSubcategory(id = UUID.randomUUID().toString(), userBusinessId = businessId, userId = userId, accountType = "asset", name = "Fixed Assets", displayOrder = 2, isSystem = true),
    AccountSubcategory(id = UUID.randomUUID().toString(), userBusinessId = businessId, userId = userId, accountType = "liability", name = "Current Liabilities", displayOrder = 3, isSystem = true),
    AccountSubcategory(id = UUID.randomUUID().toString(), userBusinessId = businessId, userId = userId, accountType = "liability", name = "Long-term Liabilities", displayOrder = 4, isSystem = true),
    AccountSubcategory(id = UUID.randomUUID().toString(), userBusinessId = businessId, userId = userId, accountType = "equity", name = "Owner's Equity", displayOrder = 5, isSystem = true),
    AccountSubcategory(id = UUID.randomUUID().toString(), userBusinessId = businessId, userId = userId, accountType = "income", name = "Operating Revenue", displayOrder = 6, isSystem = true),
    AccountSubcategory(id = UUID.randomUUID().toString(), userBusinessId = businessId, userId = userId, accountType = "cogs", name = "Cost of Goods Sold", displayOrder = 7, isSystem = true),
    AccountSubcategory(id = UUID.randomUUID().toString(), userBusinessId = businessId, userId = userId, accountType = "expense", name = "Operating Expenses", displayOrder = 8, isSystem = true),
    AccountSubcategory(id = UUID.randomUUID().toString(), userBusinessId = businessId, userId = userId, accountType = "other_income", name = "Other Income", displayOrder = 9, isSystem = true),
    AccountSubcategory(id = UUID.randomUUID().toString(), userBusinessId = businessId, userId = userId, accountType = "other_expense", name = "Other Expenses", displayOrder = 10, isSystem = true)
)

fun getDefaultChartOfAccounts(userId: String, businessId: String): List<ChartOfAccount> = listOf(
    // Assets (1000-1999)
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "1000", name = "Cash", type = "asset", accountType = "Asset", subcategory = "Current Assets"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "1010", name = "Petty Cash", type = "asset", accountType = "Asset", subcategory = "Current Assets"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "1020", name = "Bank - Checking Account", type = "asset", accountType = "Asset", subcategory = "Current Assets"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "1030", name = "Bank - Savings Account", type = "asset", accountType = "Asset", subcategory = "Current Assets"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "1100", name = "Accounts Receivable", type = "asset", accountType = "Asset", subcategory = "Current Assets"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "1200", name = "Inventory", type = "asset", accountType = "Asset", subcategory = "Current Assets"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "1300", name = "Supplies", type = "asset", accountType = "Asset", subcategory = "Current Assets"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "1400", name = "Prepaid Expenses", type = "asset", accountType = "Asset", subcategory = "Current Assets"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "1500", name = "Property, Plant & Equipment", type = "asset", accountType = "Asset", subcategory = "Fixed Assets"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "1510", name = "Furniture & Fixtures", type = "asset", accountType = "Asset", subcategory = "Fixed Assets"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "1520", name = "Equipment", type = "asset", accountType = "Asset", subcategory = "Fixed Assets"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "1530", name = "Vehicles", type = "asset", accountType = "Asset", subcategory = "Fixed Assets"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "1600", name = "Accumulated Depreciation", type = "asset", accountType = "Asset", subcategory = "Fixed Assets"),

    // Liabilities (2000-2999)
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "2000", name = "Accounts Payable", type = "liability", accountType = "Liability", subcategory = "Current Liabilities"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "2100", name = "Accrued Liabilities", type = "liability", accountType = "Liability", subcategory = "Current Liabilities"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "2110", name = "Salaries Payable", type = "liability", accountType = "Liability", subcategory = "Current Liabilities"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "2120", name = "Taxes Payable", type = "liability", accountType = "Liability", subcategory = "Current Liabilities"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "2130", name = "VAT Payable", type = "liability", accountType = "Liability", subcategory = "Current Liabilities"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "2200", name = "Notes Payable", type = "liability", accountType = "Liability", subcategory = "Long-term Liabilities"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "2210", name = "Bank Loans", type = "liability", accountType = "Liability", subcategory = "Long-term Liabilities"),

    // Equity (3000-3999)
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "3000", name = "Owner's Equity", type = "equity", accountType = "Equity", subcategory = "Owner's Equity"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "3100", name = "Retained Earnings", type = "equity", accountType = "Equity", subcategory = "Owner's Equity"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "3200", name = "Owner's Drawings", type = "equity", accountType = "Equity", subcategory = "Owner's Equity"),

    // Revenue (4000-4999)
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "4000", name = "Sales Revenue", type = "income", accountType = "Revenue", subcategory = "Operating Revenue"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "4010", name = "Service Revenue", type = "income", accountType = "Revenue", subcategory = "Operating Revenue"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "4020", name = "Product Sales", type = "income", accountType = "Revenue", subcategory = "Operating Revenue"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "4100", name = "Sales Discounts", type = "income", accountType = "Revenue", subcategory = "Operating Revenue"),

    // COGS (5000-5999)
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "5000", name = "Cost of Goods Sold", type = "cogs", accountType = "COGS", subcategory = "Cost of Goods Sold"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "5010", name = "Purchases", type = "cogs", accountType = "COGS", subcategory = "Cost of Goods Sold"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "5020", name = "Freight & Shipping", type = "cogs", accountType = "COGS", subcategory = "Cost of Goods Sold"),

    // Expenses (6000-6999)
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "6000", name = "Rent Expense", type = "expense", accountType = "Expense", subcategory = "Operating Expenses"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "6010", name = "Salaries & Wages", type = "expense", accountType = "Expense", subcategory = "Operating Expenses"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "6020", name = "Utilities", type = "expense", accountType = "Expense", subcategory = "Operating Expenses"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "6030", name = "Internet & Phone", type = "expense", accountType = "Expense", subcategory = "Operating Expenses"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "6040", name = "Office Supplies", type = "expense", accountType = "Expense", subcategory = "Operating Expenses"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "6050", name = "Marketing & Advertising", type = "expense", accountType = "Expense", subcategory = "Operating Expenses"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "6060", name = "Travel & Transport", type = "expense", accountType = "Expense", subcategory = "Operating Expenses"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "6070", name = "Insurance", type = "expense", accountType = "Expense", subcategory = "Operating Expenses"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "6080", name = "Professional Fees", type = "expense", accountType = "Expense", subcategory = "Operating Expenses"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "6090", name = "Bank Charges", type = "expense", accountType = "Expense", subcategory = "Operating Expenses"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "6100", name = "Depreciation Expense", type = "expense", accountType = "Expense", subcategory = "Operating Expenses"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "6110", name = "Repairs & Maintenance", type = "expense", accountType = "Expense", subcategory = "Operating Expenses"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "6120", name = "Licenses & Permits", type = "expense", accountType = "Expense", subcategory = "Operating Expenses"),

    // Other Income (7000-7499)
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "7000", name = "Interest Income", type = "other_income", accountType = "Other Income", subcategory = "Other Income"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "7010", name = "Rental Income", type = "other_income", accountType = "Other Income", subcategory = "Other Income"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "7020", name = "Gain on Asset Sale", type = "other_income", accountType = "Other Income", subcategory = "Other Income"),

    // Other Expense (7500-7999)
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "7500", name = "Interest Expense", type = "other_expense", accountType = "Other Expense", subcategory = "Other Expenses"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "7510", name = "Loss on Asset Sale", type = "other_expense", accountType = "Other Expense", subcategory = "Other Expenses"),
    ChartOfAccount(id = UUID.randomUUID().toString(), userId = userId, userBusinessId = businessId, code = "7520", name = "Foreign Exchange Loss", type = "other_expense", accountType = "Other Expense", subcategory = "Other Expenses")
)
