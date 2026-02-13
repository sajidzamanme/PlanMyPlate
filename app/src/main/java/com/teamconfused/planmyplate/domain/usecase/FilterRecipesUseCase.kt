package com.teamconfused.planmyplate.domain.usecase

import com.teamconfused.planmyplate.domain.repository.RecipeRepository
import com.teamconfused.planmyplate.model.Recipe

class FilterRecipesUseCase(
    private val repository: RecipeRepository
) {
    suspend fun byCalories(token: String, min: Int, max: Int): List<Recipe> {
        return repository.getRecipesByCalories(token, min, max)
    }
    
    // Add other filters as needed
}
