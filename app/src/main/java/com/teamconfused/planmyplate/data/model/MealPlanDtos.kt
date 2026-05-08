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
    val slots: List<MealSlotDto>? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class MealSlotDto(
    @SerialName("id") val slotId: Int? = null,
    val mealType: String,
    val date: String? = null,
    @SerialName("dayNumber") val dayNumber: Int? = null,
    val recipe: RecipeResponse? = null
)

@Serializable
data class CreateMealPlanRequest(
    val recipeIds: List<Int>,
    val duration: Int,
    @SerialName("startDate") val startDate: String
)

@Serializable
data class MealPlanRequest(
    @SerialName("startDate") val startDate: String,
    val duration: Int
)

@Serializable
data class GenerateMealPlanRequest(
    val userId: Int,
    val startDate: String // YYYY-MM-DD
)
