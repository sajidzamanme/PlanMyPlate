package com.teamconfused.planmyplate.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeResponse(
    val recipeId: Int? = null,
    val name: String,
    val description: String? = null,
    val calories: Int? = null,
    val prepTime: Int? = null,
    val cookTime: Int? = null,
    val servings: Int? = null,
    val instructions: String? = null,
    val recipeIngredients: List<RecipeIngredientResponse>? = null,
    val imageUrl: String? = null
)

@Serializable
data class RecipeIngredientResponse(
    val id: Int? = null,
    val quantity: Double? = null,
    val unit: String? = null,
    val ingredient: IngredientDto? = null
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
data class AdditionalMeal(
    val recipeId: Int,
    val recipe: RecipeResponse,
    val date: String,
    val mealType: String
)
