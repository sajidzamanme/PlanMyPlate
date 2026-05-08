package com.teamconfused.planmyplate.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: Int? = null,
    val name: String,
    val description: String,
    val calories: Int,
    val prepTime: Int? = null,
    val cookTime: Int? = null,
    val servings: Int? = null,
    val instructions: String? = null,
    val ingredients: List<String>? = null,
    val imageUrl: String? = null
)