package com.example.pesapilotandroid.data.repository

import com.example.pesapilotandroid.data.model.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.hours

@Singleton
class FundingRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    // Microfinance
    suspend fun getMicrofinanceInstitutions(country: String? = null): Result<List<Microfinance>> {
        return try {
            val institutions = supabaseClient.postgrest
                .from("microfinance_institutions")
                .select {
                    filter {
                        eq("is_active", true)
                        if (country != null) {
                            eq("country", country)
                        }
                    }
                }
                .decodeList<Microfinance>()
            Result.success(institutions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMicrofinanceById(id: String): Result<Microfinance?> {
        return try {
            val institution = supabaseClient.postgrest
                .from("microfinance_institutions")
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<Microfinance>()
            Result.success(institution)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Admin functions for microfinance
    suspend fun createMicrofinance(microfinance: Microfinance): Result<Microfinance> {
        return try {
            val created = supabaseClient.postgrest
                .from("microfinance_institutions")
                .insert(microfinance) {
                    select()
                }
                .decodeSingle<Microfinance>()
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMicrofinance(microfinance: Microfinance): Result<Microfinance> {
        return try {
            val updated = supabaseClient.postgrest
                .from("microfinance_institutions")
                .update(microfinance) {
                    filter {
                        eq("id", microfinance.id)
                    }
                    select()
                }
                .decodeSingle<Microfinance>()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMicrofinance(id: String): Result<Unit> {
        return try {
            supabaseClient.postgrest
                .from("microfinance_institutions")
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllMicrofinanceForAdmin(): Result<List<Microfinance>> {
        return try {
            val institutions = supabaseClient.postgrest
                .from("microfinance_institutions")
                .select()
                .decodeList<Microfinance>()
            Result.success(institutions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lenders
    suspend fun getLenders(lenderType: String? = null, country: String? = null): Result<List<Lender>> {
        return try {
            val lenders = supabaseClient.postgrest
                .from("lenders")
                .select {
                    filter {
                        if (lenderType != null) {
                            eq("type", lenderType)
                        }
                        if (country != null) {
                            eq("country", country)
                        }
                    }
                }
                .decodeList<Lender>()
            Result.success(lenders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Document Upload
    suspend fun uploadLoanDocument(
        userId: String,
        microfinanceId: String,
        documentType: String,
        fileBytes: ByteArray,
        fileName: String
    ): Result<String> {
        return try {
            val path = "loan_documents/$userId/$microfinanceId/${documentType}_$fileName"
            supabaseClient.storage
                .from("loan-documents")
                .upload(path, fileBytes) {
                    upsert = true
                }
            
            val signedUrl = supabaseClient.storage
                .from("loan-documents")
                .createSignedUrl(path, 24.hours)
            
            Result.success(signedUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSignedDocumentUrl(path: String): Result<String> {
        return try {
            val signedUrl = supabaseClient.storage
                .from("loan-documents")
                .createSignedUrl(path, 24.hours)
            Result.success(signedUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Loan Applications
    suspend fun createLoanApplication(application: LoanApplication): Result<LoanApplication> {
        return try {
            val created = supabaseClient.postgrest
                .from("loan_applications")
                .insert(application) {
                    select()
                }
                .decodeSingle<LoanApplication>()
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserLoanApplications(userId: String): Result<List<LoanApplication>> {
        return try {
            val applications = supabaseClient.postgrest
                .from("loan_applications")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<LoanApplication>()
            Result.success(applications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Upload microfinance logo (admin)
    suspend fun uploadMicrofinanceLogo(
        microfinanceId: String,
        fileBytes: ByteArray,
        fileName: String
    ): Result<String> {
        return try {
            val path = "microfinance_logos/$microfinanceId/$fileName"
            supabaseClient.storage
                .from("microfinance-logos")
                .upload(path, fileBytes) {
                    upsert = true
                }
            
            val publicUrl = supabaseClient.storage
                .from("microfinance-logos")
                .publicUrl(path)
            
            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
