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
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val dateOfBirth: String,
    val age: Int? = null,
    val weight: Float? = null,
    val budget: Float? = null
)

@Serializable
data class UpdateUserRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val dateOfBirth: String? = null,
    val age: Int? = null,
    val weight: Float? = null,
    val budget: Float? = null
)

@Serializable
data class UserPreferencesRequest(
    val userId: Int,
    val diet: String? = null,
    val allergies: List<String>? = null,
    val dislikes: List<String>? = null,
    val servings: Int? = null,
    val budget: Float? = null
)

@Serializable
data class UserPreferencesResponse(
    @SerialName("pref_id") val prefId: Int? = null,
    @SerialName("user_id") val userId: Int? = null,
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
    @SerialName("diet_id") val dietId: Int,
    @SerialName("diet_name") val dietName: String
)

@Serializable
data class AllergyDto(
    @SerialName("allergy_id") val allergyId: Int,
    @SerialName("allergy_name") val allergyName: String
)
