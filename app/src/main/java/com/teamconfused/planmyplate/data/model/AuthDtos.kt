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
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    @SerialName("access_token") val accessToken: String? = null,
    @SerialName("token_type") val tokenType: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val userId: Int? = null,
    val phone: String? = null,
    val dateOfBirth: String? = null,
    val message: String? = null
)

@Serializable
data class ForgotPasswordRequest(
    val email: String
)

@Serializable
data class ForgotPasswordResponse(
    val message: String? = null
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
