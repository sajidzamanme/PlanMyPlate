package com.teamconfused.planmyplate.domain.repository

import com.teamconfused.planmyplate.model.GenerateRecipeRequest
import com.teamconfused.planmyplate.model.MealPlan
import com.teamconfused.planmyplate.model.Recipe

interface AiRepository {
    suspend fun generateRecipe(token: String, request: GenerateRecipeRequest): Recipe
    suspend fun generateMealPlan(token: String, userId: Int, startDate: String? = null): MealPlan
}
