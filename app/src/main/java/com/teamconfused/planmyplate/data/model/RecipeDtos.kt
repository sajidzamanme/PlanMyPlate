package com.teamconfused.planmyplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeResponse(
    @SerialName("recipe_id") val id: Int? = null,
    val name: String,
    val description: String? = null,
    val calories: Int? = null,
    @SerialName("prep_time") val prepTime: Int? = null,
    @SerialName("cook_time") val cookTime: Int? = null,
    val servings: Int? = null,
    val instructions: String? = null,
    @SerialName("recipe_ingredients") val ingredients: List<RecipeIngredientResponse>? = null,
    @SerialName("image_url") val imageUrl: String? = null
)

@Serializable
data class RecipeIngredientResponse(
    val id: Int? = null,
    val quantity: Double? = null,
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