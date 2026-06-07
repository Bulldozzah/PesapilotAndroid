package com.example.pesapilotandroid.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

// Matches: public.business_categories table
@Serializable
data class BusinessCategory(
    val id: String = "",
    val name: String = "",
    val slug: String = "",
    val description: String? = null,
    val icon: String? = null,
    val emoji: String? = null,
    @SerialName("sort_order")
    val sortOrder: Int = 0,
    @SerialName("created_at")
    val createdAt: String? = null
)

// Matches: public.business_templates table
@Serializable
data class BusinessTemplate(
    val id: String = "",
    @SerialName("category_id")
    val categoryId: String? = null,
    val name: String = "",
    val slug: String = "",
    val description: String? = null,
    @SerialName("startup_cost_min")
    val startupCostMin: Double = 0.0,
    @SerialName("startup_cost_max")
    val startupCostMax: Double = 0.0,
    @SerialName("monthly_profit_min")
    val monthlyProfitMin: Double = 0.0,
    @SerialName("monthly_profit_max")
    val monthlyProfitMax: Double = 0.0,
    val difficulty: String = "medium",
    @SerialName("time_to_profit_months")
    val timeToProfitMonths: Int = 3,
    val currency: String = "KES",
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("overview_content")
    val overviewContent: String? = null,
    @SerialName("overview_video_url")
    val overviewVideoUrl: String? = null,
    @SerialName("overview_web_url")
    val overviewWebUrl: String? = null,
    @SerialName("overview_pdf_url")
    val overviewPdfUrl: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
) {
    // Helper for backward compatibility
    val difficultyLevel: String get() = difficulty
    val estimatedStartupCost: Double get() = startupCostMin
    val estimatedRevenue: Double get() = monthlyProfitMin
}

// Matches: public.business_template_steps table
@Serializable
data class BusinessTemplateStep(
    val id: String = "",
    @SerialName("template_id")
    val templateId: String = "",
    @SerialName("step_number")
    val stepNumber: Int = 0,
    val title: String = "",
    val description: String? = null,
    @SerialName("est_days")
    val estDays: Int = 1
)

// Matches: public.user_businesses table
@Serializable
data class UserBusiness(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("template_id")
    val templateId: String? = null,
    val name: String = "",
    val description: String? = null,
    val currency: String = "KES",
    val budget: Double = 10000.0,
    @SerialName("start_date")
    val startDate: String? = null,
    @SerialName("expected_monthly_profit")
    val expectedMonthlyProfit: Double? = null,
    @SerialName("started_at")
    val startedAt: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

// Matches: public.step_progress table
@Serializable
data class StepProgress(
    val id: Long = 0,
    @SerialName("user_business_id")
    val userBusinessId: String = "",
    @SerialName("step_number")
    val stepNumber: Int = 0,
    @SerialName("step_title")
    val stepTitle: String? = null,
    val completed: Boolean = false,
    @SerialName("completed_at")
    val completedAt: String? = null,
    val notes: String? = null,
    @SerialName("checklist_status")
    val checklistStatus: String = "[]",
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

// Matches: public.user_roadmap_progress table (legacy)
@Serializable
data class UserRoadmapProgress(
    val id: String = "",
    @SerialName("user_business_id")
    val userBusinessId: String = "",
    @SerialName("step_id")
    val stepId: String = "",
    val completed: Boolean = false,
    @SerialName("completed_at")
    val completedAt: String? = null,
    val notes: String? = null
)

// Matches: public.country_authorities table
@Serializable
data class CountryAuthority(
    val id: String = "",
    @SerialName("country_code")
    val countryCode: String = "",
    @SerialName("country_name")
    val countryName: String = "",
    @SerialName("authority_name")
    val authorityName: String = "",
    @SerialName("authority_website")
    val authorityWebsite: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

// Matches: public.business_plans table
@Serializable
data class BusinessPlan(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("user_business_id")
    val userBusinessId: String? = null,
    val title: String = "",
    val content: String = "{}",
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
) {
    // Helper for backward compatibility
    val businessName: String get() = title
}

// Matches: public.regulatory_authorities table
@Serializable
data class RegulatoryAuthority(
    val id: String = "",
    val country: String = "",
    val name: String = "",
    val description: String? = null,
    val website: String? = null,
    val category: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

enum class BusinessStatus(val value: String) {
    IDEA("idea"),
    PLANNING("planning"),
    SETUP("setup"),
    LAUNCHED("launched"),
    GROWING("growing")
}

enum class DifficultyLevel(val value: String) {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard")
}

enum class StepStatus(val value: String) {
    PENDING("pending"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed")
}
