package com.example.pesapilotandroid.data.repository

import com.example.pesapilotandroid.data.model.UserProfile
import com.example.pesapilotandroid.data.model.UserRole
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    private val auth: Auth get() = supabaseClient.auth

    val currentUser: Flow<UserInfo?> = auth.sessionStatus.map { status ->
        when (status) {
            is io.github.jan.supabase.auth.status.SessionStatus.Authenticated -> status.session.user
            else -> null
        }
    }

    val isAuthenticated: Flow<Boolean> = auth.sessionStatus.map { status ->
        status is io.github.jan.supabase.auth.status.SessionStatus.Authenticated
    }

    suspend fun signUp(email: String, password: String, fullName: String): Result<Unit> {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = kotlinx.serialization.json.buildJsonObject {
                    put("full_name", kotlinx.serialization.json.JsonPrimitive(fullName))
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.resetPasswordForEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUserOrNull()?.id
    }

    suspend fun getUserProfile(userId: String): Result<UserProfile?> {
        return try {
            val profile = supabaseClient.postgrest
                .from("profiles")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<UserProfile>()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUserProfile(profile: UserProfile): Result<UserProfile> {
        return try {
            // Profile is auto-created by trigger, so we just update it
            val created = supabaseClient.postgrest
                .from("profiles")
                .upsert(profile) {
                    select()
                }
                .decodeSingle<UserProfile>()
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(profile: UserProfile): Result<UserProfile> {
        return try {
            val updated = supabaseClient.postgrest
                .from("profiles")
                .update(profile) {
                    filter {
                        eq("id", profile.id)
                    }
                    select()
                }
                .decodeSingle<UserProfile>()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserRole(userId: String): Result<UserRole?> {
        return try {
            val role = supabaseClient.postgrest
                .from("user_roles")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeSingleOrNull<UserRole>()
            Result.success(role)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isAdmin(userId: String): Boolean {
        return getUserRole(userId).getOrNull()?.role == "admin"
    }
}
