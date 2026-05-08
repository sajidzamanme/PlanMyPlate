package com.teamconfused.planmyplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MealPlanDto(
    @SerialName("mpId") val mpId: Int? = null,
    @SerialName("userId") val userId: Int? = null,
    @SerialName("startDate") val startDate: String? = null,
    val duration: Int,
    val status: String = "active",
    val slots: List<MealSlotDto>? = null
)

@Serializable
data class MealSlotDto(
    val id: Int? = null,
    val slotIndex: Int? = null,
    val mealType: String,
    val date: String? = null,
    val dayNumber: Int? = null,
    val servingsMultiplier: Int? = null,
    val recipe: RecipeResponse? = null
)

@Serializable
data class CreateMealPlanRequest(
    val recipeIds: List<Int>,
    val servingsMultipliers: List<Int>? = null,
    val duration: Int,
    @SerialName("startDate") val startDate: String
)

@Serializable
data class MealPlanUpdateRequest(
    val status: String? = null,
    val duration: Int? = null
)

@Serializable
data class GenerateMealPlanRequest(
    val userId: Int,
    val startDate: String // YYYY-MM-DD
)
