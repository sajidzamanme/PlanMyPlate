package com.teamconfused.planmyplate.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val userId: Int? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val dateOfBirth: String? = null,
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
    val prefId: Int? = null,
    val userId: Int? = null,
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
    val dietId: Int? = null,
    val dietName: String? = null
)

@Serializable
data class AllergyDto(
    val allergyId: Int? = null,
    val allergyName: String? = null
)
