package com.teamconfused.planmyplate.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    @SerialName("recipeId") val id: Int? = null,
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

// Extension function to convert RecipeResponse to Recipe
fun RecipeResponse.toRecipe(): Recipe {
    return Recipe(
        id = this.id,
        name = this.name,
        description = this.description ?: "",
        calories = this.calories ?: 0,
        prepTime = this.prepTime,
        cookTime = this.cookTime,
        servings = this.servings,
        instructions = this.instructions,
        ingredients = this.ingredients?.map { 
            val qty = if (it.quantity != null && it.quantity > 0) "${it.quantity} " else ""
            val unit = if (!it.unit.isNullOrBlank()) "${it.unit} " else ""
            val name = it.ingredient?.name ?: "Unknown Ingredient"
            "$qty$unit$name".trim()
        },
        imageUrl = this.imageUrl
    )
}