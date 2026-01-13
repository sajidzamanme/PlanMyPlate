package com.teamconfused.planmyplate.model

import kotlinx.serialization.Serializable

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
    val token: String? = null,
    val message: String? = null,
    val name: String? = null,
    val email: String? = null
)