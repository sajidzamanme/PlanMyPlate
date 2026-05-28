package com.teamconfused.planmyplate.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MealPlanDto(
    val mpId: Int? = null,
    val userId: Int? = null,
    val startDate: String? = null,
    val duration: Int,
    val status: String = "active",
    val slots: List<MealSlotDto>? = null
)

@Serializable
data class MealSlotDto(
    val id: Int? = null,
    val slotIndex: Int? = null,
    val mealType: String,
    val dayNumber: Int? = null,
    val servingsMultiplier: Int? = null,
    val recipe: RecipeResponse? = null
)

@Serializable
data class CreateMealPlanRequest(
    val recipeIds: List<Int>,
    val servingsMultipliers: List<Int>? = null,
    val duration: Int,
    val startDate: String
)

@Serializable
data class MealPlanUpdateRequest(
    val status: String? = null,
    val duration: Int? = null
)
