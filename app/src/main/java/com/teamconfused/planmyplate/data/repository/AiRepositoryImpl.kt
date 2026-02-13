package com.teamconfused.planmyplate.data.repository

import com.teamconfused.planmyplate.domain.repository.AiRepository
import com.teamconfused.planmyplate.model.GenerateRecipeRequest
import com.teamconfused.planmyplate.model.MealPlan
import com.teamconfused.planmyplate.model.Recipe
import com.teamconfused.planmyplate.network.AiService

class AiRepositoryImpl(
    private val api: AiService
) : AiRepository {
    override suspend fun generateRecipe(token: String, request: GenerateRecipeRequest): Recipe {
        return api.generateRecipe(token, request)
    }

    override suspend fun generateMealPlan(token: String, userId: Int, startDate: String?): MealPlan {
        return api.generateMealPlan(token, userId, startDate)
    }
}
