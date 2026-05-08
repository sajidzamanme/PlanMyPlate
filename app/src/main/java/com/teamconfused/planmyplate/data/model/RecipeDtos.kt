package com.teamconfused.planmyplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeResponse(
    @SerialName("recipeId") val id: Int? = null,
    val name: String,
    val description: String? = null,
    val calories: Int? = null,
    val prepTime: Int? = null,
    val cookTime: Int? = null,
    val servings: Int? = null,
    val instructions: String? = null,
    @SerialName("recipeIngredients") val ingredients: List<RecipeIngredientResponse>? = null,
    val imageUrl: String? = null
)

@Serializable
data class RecipeIngredientResponse(
    val quantity: Int? = null,
    val unit: String? = null,
    val ingredient: IngredientDto? = null
)

@Serializable
data class RecipeRequest(
    val name: String,
    val description: String? = null,
    val calories: Int? = null,
    val imageUrl: String? = null
)

@Serializable
data class CreateRecipeRequest(
    val name: String,
    val description: String? = null,
    val calories: Int? = null,
    val prepTime: Int? = null,
    val cookTime: Int? = null,
    val servings: Int? = null,
    val instructions: String? = null,
    val imageUrl: String? = null,
    val ingredients: List<RecipeIngredientRequest>? = null
)

@Serializable
data class RecipeIngredientRequest(
    val ingId: Int,
    val quantity: Int,
    val unit: String
)

@Serializable
data class ImageUploadResponse(
    val url: String,
    val filename: String
)

@Serializable
data class GenerateRecipeRequest(
    @SerialName("availableIngredients") val availableIngredients: List<String> = emptyList(),
    @SerialName("maxCalories") val maxCalories: Int? = null,
    @SerialName("cuisineType") val cuisineType: String? = null,
    val allergies: List<String> = emptyList(),
    @SerialName("dietaryPreference") val dietaryPreference: String? = null,
    val mood: String? = null,
    val servings: Int = 2,
    @SerialName("maxCookingTime") val maxCookingTime: Int? = null
)

@Serializable
data class AdditionalMeal(
    @SerialName("recipeId") val recipeId: Int,
    val recipe: RecipeResponse,
    val date: String,
    @SerialName("mealType") val mealType: String
)
