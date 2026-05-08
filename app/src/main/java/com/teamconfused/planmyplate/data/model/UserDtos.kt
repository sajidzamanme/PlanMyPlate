package com.teamconfused.planmyplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRefDto(
    @SerialName("userId") val userId: Int
)

@Serializable
data class UserDto(
    @SerialName("userId") val userId: Int,
    @SerialName("userName") val userName: String? = null,
    val name: String,
    val email: String,
    val password: String? = null,
    val age: Int? = null,
    val weight: Float? = null,
    val budget: Float? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class UpdateUserRequest(
    val name: String? = null,
    @SerialName("userName") val userName: String? = null,
    val age: Int? = null,
    val weight: Float? = null,
    val budget: Float? = null
)

@Serializable
data class UserPreferencesRequest(
    val diet: String? = null,
    val allergies: List<String>? = null,
    val dislikes: List<String>? = null,
    val servings: Int? = null,
    val budget: Float? = null
)

@Serializable
data class UserPreferencesResponse(
    @SerialName("prefId") val prefId: Int? = null,
    @SerialName("userId") val userId: Int? = null,
    val diet: String? = null,
    val allergies: List<String>? = null,
    val dislikes: List<String>? = null,
    val servings: Int? = null,
    val budget: Float? = null,
    val age: Int? = null,
    val weight: Float? = null
)

@Serializable
data class DietDto(
    val dietId: Int,
    val dietName: String
)

@Serializable
data class AllergyDto(
    val allergyId: Int,
    val allergyName: String
)
