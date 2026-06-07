package com.example.pesapilotandroid.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Matches: public.profiles table
@Serializable
data class UserProfile(
    val id: String = "",
    @SerialName("full_name")
    val fullName: String = "",
    val country: String = "Kenya",
    @SerialName("country_code")
    val countryCode: String? = null,
    val currency: String = "KES",
    val phone: String? = null,
    @SerialName("business_name")
    val businessName: String? = null,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("completed_onboarding")
    val completedOnboarding: Boolean = false,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
) {
    // Helper for backward compatibility
    val phoneNumber: String get() = phone ?: ""
    val isOnboarded: Boolean get() = completedOnboarding
}

@Serializable
data class UserRole(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    val role: String = "user",
    @SerialName("created_at")
    val createdAt: String? = null
)

enum class Role(val value: String) {
    USER("user"),
    ADMIN("admin")
}
