package com.example.pesapilotandroid.data.repository

import com.example.pesapilotandroid.data.model.*
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusinessRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    // Business Categories
    suspend fun getBusinessCategories(): Result<List<BusinessCategory>> {
        return try {
            val categories = supabaseClient.postgrest
                .from("business_categories")
                .select()
                .decodeList<BusinessCategory>()
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Business Templates
    suspend fun getBusinessTemplates(): Result<List<BusinessTemplate>> {
        return try {
            val templates = supabaseClient.postgrest
                .from("business_templates")
                .select()
                .decodeList<BusinessTemplate>()
            Result.success(templates)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBusinessTemplatesByCategory(categoryId: String): Result<List<BusinessTemplate>> {
        return try {
            val templates = supabaseClient.postgrest
                .from("business_templates")
                .select {
                    filter {
                        eq("category_id", categoryId)
                    }
                }
                .decodeList<BusinessTemplate>()
            Result.success(templates)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBusinessTemplateById(templateId: String): Result<BusinessTemplate?> {
        return try {
            val template = supabaseClient.postgrest
                .from("business_templates")
                .select {
                    filter {
                        eq("id", templateId)
                    }
                }
                .decodeSingleOrNull<BusinessTemplate>()
            Result.success(template)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBusinessTemplateSteps(templateId: String): Result<List<BusinessTemplateStep>> {
        return try {
            val steps = supabaseClient.postgrest
                .from("business_template_steps")
                .select {
                    filter {
                        eq("template_id", templateId)
                    }
                    order("step_number", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                }
                .decodeList<BusinessTemplateStep>()
            Result.success(steps)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // User Businesses
    suspend fun getUserBusinesses(userId: String): Result<List<UserBusiness>> {
        return try {
            val businesses = supabaseClient.postgrest
                .from("user_businesses")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<UserBusiness>()
            Result.success(businesses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUserBusiness(business: UserBusiness): Result<UserBusiness> {
        return try {
            val created = supabaseClient.postgrest
                .from("user_businesses")
                .insert(business) {
                    select()
                }
                .decodeSingle<UserBusiness>()
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserBusiness(business: UserBusiness): Result<UserBusiness> {
        return try {
            val updated = supabaseClient.postgrest
                .from("user_businesses")
                .update(business) {
                    filter {
                        eq("id", business.id)
                    }
                    select()
                }
                .decodeSingle<UserBusiness>()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUserBusiness(businessId: String): Result<Unit> {
        return try {
            supabaseClient.postgrest
                .from("user_businesses")
                .delete {
                    filter {
                        eq("id", businessId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Step Progress (uses step_progress table)
    suspend fun getStepProgress(userBusinessId: String): Result<List<StepProgress>> {
        return try {
            val progress = supabaseClient.postgrest
                .from("step_progress")
                .select {
                    filter {
                        eq("user_business_id", userBusinessId)
                    }
                    order("step_number", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                }
                .decodeList<StepProgress>()
            Result.success(progress)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStepProgress(progress: StepProgress): Result<StepProgress> {
        return try {
            val updated = supabaseClient.postgrest
                .from("step_progress")
                .upsert(progress) {
                    select()
                }
                .decodeSingle<StepProgress>()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Legacy roadmap progress (user_roadmap_progress table)
    suspend fun getRoadmapProgress(userBusinessId: String): Result<List<UserRoadmapProgress>> {
        return try {
            val progress = supabaseClient.postgrest
                .from("user_roadmap_progress")
                .select {
                    filter {
                        eq("user_business_id", userBusinessId)
                    }
                }
                .decodeList<UserRoadmapProgress>()
            Result.success(progress)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Country Authorities
    suspend fun getCountryAuthority(countryCode: String): Result<CountryAuthority?> {
        return try {
            val authority = supabaseClient.postgrest
                .from("country_authorities")
                .select {
                    filter {
                        eq("country_code", countryCode)
                    }
                }
                .decodeSingleOrNull<CountryAuthority>()
            Result.success(authority)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Business Plans
    suspend fun getBusinessPlans(userId: String): Result<List<BusinessPlan>> {
        return try {
            val plans = supabaseClient.postgrest
                .from("business_plans")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<BusinessPlan>()
            Result.success(plans)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createBusinessPlan(plan: BusinessPlan): Result<BusinessPlan> {
        return try {
            val created = supabaseClient.postgrest
                .from("business_plans")
                .insert(plan) {
                    select()
                }
                .decodeSingle<BusinessPlan>()
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBusinessPlan(plan: BusinessPlan): Result<BusinessPlan> {
        return try {
            val updated = supabaseClient.postgrest
                .from("business_plans")
                .update(plan) {
                    filter {
                        eq("id", plan.id)
                    }
                    select()
                }
                .decodeSingle<BusinessPlan>()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBusinessPlan(planId: String): Result<Unit> {
        return try {
            supabaseClient.postgrest
                .from("business_plans")
                .delete {
                    filter {
                        eq("id", planId)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
