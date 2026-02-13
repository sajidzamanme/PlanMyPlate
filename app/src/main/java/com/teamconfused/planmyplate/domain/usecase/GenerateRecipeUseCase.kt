package com.teamconfused.planmyplate.domain.usecase

import com.teamconfused.planmyplate.domain.repository.AiRepository
import com.teamconfused.planmyplate.model.GenerateRecipeRequest
import com.teamconfused.planmyplate.model.Recipe

class GenerateRecipeUseCase(
    private val aiRepository: AiRepository
) {
    suspend operator fun invoke(
        token: String,
        ingredients: List<String>,
        mood: String?,
        dietaryPreference: String?,
        maxCalories: Int
    ): Recipe {
        val request = GenerateRecipeRequest(
            availableIngredients = ingredients,
            mood = mood,
            dietaryPreference = dietaryPreference,
            maxCalories = maxCalories
        )
        return aiRepository.generateRecipe(token, request)
    }
}
