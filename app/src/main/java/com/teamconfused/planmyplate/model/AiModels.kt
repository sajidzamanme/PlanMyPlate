package com.teamconfused.planmyplate.model

import kotlinx.serialization.Serializable

@Serializable
data class GenerateRecipeRequest(
    val availableIngredients: List<String> = emptyList(),
    val maxCalories: Int? = null,
    val cuisineType: String? = null,
    val allergies: List<String> = emptyList(),
    val dietaryPreference: String? = null,
    val mood: String? = null,
    val servings: Int = 2,
    val maxCookingTime: Int? = null
)

@Serializable
data class GenerateMealPlanRequest(
    val userId: Int,
    val startDate: String // YYYY-MM-DD
)
