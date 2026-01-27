package com.teamconfused.planmyplate.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.intOrNull

@Serializable
data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class SigninRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    @SerialName("userId") val userId: JsonElement? = null,
    @SerialName("id") val id: JsonElement? = null,
    @SerialName("user_id") val userIdAlternative: JsonElement? = null,
    val token: String? = null,
    val message: String? = null,
    val name: String? = null,
    val email: String? = null
) {
    fun getEffectiveUserId(): Int? {
        val element = userId ?: id ?: userIdAlternative
        return try {
            element?.jsonPrimitive?.intOrNull ?: element?.jsonPrimitive?.content?.toIntOrNull()
        } catch (e: Exception) {
            null
        }
    }
}

@Serializable
data class ForgotPasswordRequest(
    val email: String
)

@Serializable
data class ForgotPasswordResponse(
    val message: String,
    val token: String? = null
)

@Serializable
data class ResetPasswordRequest(
    val resetToken: String,
    val newPassword: String
)

@Serializable
data class ResetPasswordResponse(
    val message: String
)

@Serializable
data class UserPreferencesDto(
    val diet: String? = null,
    val allergies: String? = null, // Changed from List to String
    val dislikes: String? = null,  // Changed from List to String
    val servings: Int? = null,
    val budget: Float? = null,
    val age: Int? = null,
    val weight: Float? = null
)
