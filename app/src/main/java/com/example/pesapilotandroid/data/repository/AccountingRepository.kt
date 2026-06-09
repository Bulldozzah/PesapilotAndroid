package com.example.pesapilotandroid.data.repository

import com.example.pesapilotandroid.data.model.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountingRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    // Chart of Accounts
    suspend fun getChartOfAccounts(userId: String, businessId: String? = null): Result<List<ChartOfAccount>> {
        return try {
            val accounts = supabaseClient.postgrest
                .from("chart_of_accounts")
                .select {
                    filter {
                        eq("user_id", userId)
                        if (businessId != null) {
                            eq("user_business_id", businessId)
                        }
                    }
                    order("code", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                }
                .decodeList<ChartOfAccount>()
            Result.success(accounts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createAccount(account: ChartOfAccount): Result<ChartOfAccount> {
        return try {
            val created = supabaseClient.postgrest
                .from("chart_of_accounts")
                .insert(account) {
                    select()
                }
                .decodeSingle<ChartOfAccount>()
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateAccount(account: ChartOfAccount): Result<ChartOfAccount> {
        return try {
            val updated = supabaseClient.postgrest
                .from("chart_of_accounts")
                .update(account) {
                    filter {
                        eq("id", account.id)
                    }
                    select()
                }
                .decodeSingle<ChartOfAccount>()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Journal Entries
    suspend fun getJournalEntries(userId: String, businessId: String? = null): Result<List<JournalEntry>> {
        return try {
            val entries = supabaseClient.postgrest
                .from("journal_entries")
                .select {
                    filter {
                        eq("user_id", userId)
                        if (businessId != null) {
                            eq("user_business_id", businessId)
                        }
                    }
                    order("entry_date", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                }
                .decodeList<JournalEntry>()
            Result.success(entries)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getJournalEntry(entryId: String): Result<JournalEntry?> {
        return try {
            val entry = supabaseClient.postgrest
                .from("journal_entries")
                .select {
                    filter {
                        eq("id", entryId)
                    }
                }
                .decodeSingleOrNull<JournalEntry>()
            Result.success(entry)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createJournalEntry(entry: JournalEntry): Result<JournalEntry> {
        return try {
            val created = supabaseClient.postgrest
                .from("journal_entries")
                .insert(entry) {
                    select()
                }
                .decodeSingle<JournalEntry>()
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateJournalEntry(entry: JournalEntry): Result<JournalEntry> {
        return try {
            val updated = supabaseClient.postgrest
                .from("journal_entries")
                .update(entry) {
                    filter {
                        eq("id", entry.id)
                    }
                    select()
                }
                .decodeSingle<JournalEntry>()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteJournalEntry(entryId: String): Result<Unit> {
        return try {
            // Delete lines first (cascade should handle this, but being explicit)
            supabaseClient.postgrest
                .from("journal_lines")
                .delete {
                    filter {
                        eq("journal_entry_id", entryId)
                    }
                }
            // Then delete entry
            supabaseClient.postgrest
                .from("journal_entries")
                .delete {
                    filter {
                        eq("id", entryId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Journal Entry Lines
    suspend fun getJournalEntryLines(entryId: String): Result<List<JournalEntryLine>> {
        return try {
            val lines = supabaseClient.postgrest
                .from("journal_lines")
                .select {
                    filter {
                        eq("journal_entry_id", entryId)
                    }
                }
                .decodeList<JournalEntryLine>()
            Result.success(lines)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createJournalEntryLines(lines: List<JournalEntryLine>): Result<List<JournalEntryLine>> {
        return try {
            val created = supabaseClient.postgrest
                .from("journal_lines")
                .insert(lines) {
                    select()
                }
                .decodeList<JournalEntryLine>()
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Bank Accounts
    suspend fun getBankAccounts(userId: String, businessId: String? = null): Result<List<BankAccount>> {
        return try {
            val accounts = supabaseClient.postgrest
                .from("bank_accounts")
                .select {
                    filter {
                        eq("user_id", userId)
                        if (businessId != null) {
                            eq("user_business_id", businessId)
                        }
                    }
                }
                .decodeList<BankAccount>()
            Result.success(accounts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createBankAccount(account: BankAccount): Result<BankAccount> {
        return try {
            val created = supabaseClient.postgrest
                .from("bank_accounts")
                .insert(account) {
                    select()
                }
                .decodeSingle<BankAccount>()
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBankAccount(account: BankAccount): Result<BankAccount> {
        return try {
            val updated = supabaseClient.postgrest
                .from("bank_accounts")
                .update(account) {
                    filter {
                        eq("id", account.id)
                    }
                    select()
                }
                .decodeSingle<BankAccount>()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBankAccount(accountId: String): Result<Unit> {
        return try {
            supabaseClient.postgrest
                .from("bank_accounts")
                .delete {
                    filter {
                        eq("id", accountId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Account Subcategories
    suspend fun getAccountSubcategories(userId: String, businessId: String): Result<List<AccountSubcategory>> {
        return try {
            val subs = supabaseClient.postgrest
                .from("account_subcategories")
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("user_business_id", businessId)
                    }
                    order("display_order", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                }
                .decodeList<AccountSubcategory>()
            Result.success(subs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createAccountSubcategory(sub: AccountSubcategory): Result<AccountSubcategory> {
        return try {
            val created = supabaseClient.postgrest
                .from("account_subcategories")
                .insert(sub) { select() }
                .decodeSingle<AccountSubcategory>()
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAccountSubcategory(subId: String): Result<Unit> {
        return try {
            supabaseClient.postgrest.from("account_subcategories").delete { filter { eq("id", subId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Seed default COA
    suspend fun seedDefaultAccounts(userId: String, businessId: String): Result<Unit> {
        return try {
            val existing = supabaseClient.postgrest
                .from("chart_of_accounts")
                .select { filter { eq("user_business_id", businessId) } }
                .decodeList<ChartOfAccount>()
            val existingCodes = existing.map { it.code }.toSet()

            val defaults = getDefaultChartOfAccounts(userId, businessId)
                .filter { it.code !in existingCodes }

            if (defaults.isNotEmpty()) {
                supabaseClient.postgrest.from("chart_of_accounts").insert(defaults)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun seedDefaultSubcategories(userId: String, businessId: String): Result<Unit> {
        return try {
            val defaults = getDefaultSubcategories(userId, businessId)
            for (sub in defaults) {
                try {
                    supabaseClient.postgrest.from("account_subcategories").insert(sub)
                } catch (_: Exception) { /* skip duplicates */ }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get entries with lines and account info
    suspend fun getJournalEntriesWithLines(userId: String, businessId: String): Result<List<JournalEntry>> {
        return try {
            val entries = supabaseClient.postgrest
                .from("journal_entries")
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("user_business_id", businessId)
                    }
                    order("entry_date", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                }
                .decodeList<JournalEntry>()
            Result.success(entries)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLinesForEntries(entryIds: List<String>): Result<List<JournalEntryLine>> {
        if (entryIds.isEmpty()) return Result.success(emptyList())
        return try {
            val lines = supabaseClient.postgrest
                .from("journal_lines")
                .select {
                    filter {
                        isIn("journal_entry_id", entryIds)
                    }
                }
                .decodeList<JournalEntryLine>()
            Result.success(lines)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteJournalEntryLines(entryId: String): Result<Unit> {
        return try {
            supabaseClient.postgrest.from("journal_lines").delete { filter { eq("journal_entry_id", entryId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Contacts
    suspend fun getContacts(userId: String, businessId: String? = null, contactType: String? = null): Result<List<Contact>> {
        return try {
            val contacts = supabaseClient.postgrest
                .from("contacts")
                .select {
                    filter {
                        eq("user_id", userId)
                        if (businessId != null) {
                            eq("user_business_id", businessId)
                        }
                        if (contactType != null) {
                            eq("type", contactType)
                        }
                    }
                    order("name", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                }
                .decodeList<Contact>()
            Result.success(contacts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createContact(contact: Contact): Result<Contact> {
        return try {
            val created = supabaseClient.postgrest
                .from("contacts")
                .insert(contact) {
                    select()
                }
                .decodeSingle<Contact>()
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateContact(contact: Contact): Result<Contact> {
        return try {
            val updated = supabaseClient.postgrest
                .from("contacts")
                .update(contact) {
                    filter {
                        eq("id", contact.id)
                    }
                    select()
                }
                .decodeSingle<Contact>()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteContact(contactId: String): Result<Unit> {
        return try {
            supabaseClient.postgrest
                .from("contacts")
                .delete {
                    filter {
                        eq("id", contactId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
