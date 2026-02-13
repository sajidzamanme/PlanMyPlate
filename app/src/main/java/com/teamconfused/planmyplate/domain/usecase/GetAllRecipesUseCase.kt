package com.teamconfused.planmyplate.domain.usecase

import com.teamconfused.planmyplate.domain.repository.RecipeRepository
import com.teamconfused.planmyplate.model.Recipe

class GetAllRecipesUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(): List<Recipe> {
        return repository.getAllRecipes()
    }
}
