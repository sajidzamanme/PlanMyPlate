package com.teamconfused.planmyplate.model

data class Recipe(
    val id: Int? = null,
    val name: String,
    val description: String,
    val calories: Int,
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
        ingredients = null,
        imageUrl = null
    )
}