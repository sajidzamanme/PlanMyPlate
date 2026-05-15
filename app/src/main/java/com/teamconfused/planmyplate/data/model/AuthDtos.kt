package com.teamconfused.planmyplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignupRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val phone: String,
    val dateOfBirth: String
)

@Serializable
data class SigninRequest(
    val email: String, // Can be email or phone according to docs
    val password: String
)

@Serializable
data class AuthResponse(
    @SerialName("access_token") val accessToken: String? = null,
    @SerialName("token_type") val tokenType: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    @SerialName("user_id") val userId: Int? = null,
    val phone: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    val message: String? = null
)

@Serializable
data class ForgotPasswordRequest(
    val email: String
)

@Serializable
data class ForgotPasswordResponse(
    val message: String? = null,
    val token: String? = null
)

@Serializable
data class ResetPasswordRequest(
    val resetToken: String,
    val newPassword: String
)

@Serializable
data class ResetPasswordResponse(
    val message: String? = null
)

@Serializable
data class UserPreferencesDto(
    val diet: String? = null,
    val allergies: String? = null,
    val dislikes: String? = null,
    val servings: Int? = null,
    val budget: Float? = null,
    val age: Int? = null,
    val weight: Float? = null
)
