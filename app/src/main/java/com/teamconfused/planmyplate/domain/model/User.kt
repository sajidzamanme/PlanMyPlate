package com.teamconfused.planmyplate.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: Int,
    val userName: String? = null,
    val name: String,
    val email: String,
    val age: Int? = null,
    val weight: Float? = null,
    val budget: Float? = null,
    val createdAt: String? = null
)

@Serializable
data class UserPreferences(
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
data class Diet(
    val dietId: Int,
    val dietName: String
)

@Serializable
data class Allergy(
    val allergyId: Int,
    val allergyName: String
)
