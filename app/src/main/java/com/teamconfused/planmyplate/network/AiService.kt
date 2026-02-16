package com.teamconfused.planmyplate.network

import com.teamconfused.planmyplate.model.GenerateMealPlanRequest
import com.teamconfused.planmyplate.model.GenerateRecipeRequest
import com.teamconfused.planmyplate.model.MealPlan
import com.teamconfused.planmyplate.model.Recipe
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AiService {
    @POST("api/ai/generate-recipe")
    suspend fun generateRecipe(
        @Header("Authorization") token: String,
        @Body request: GenerateRecipeRequest
    ): com.teamconfused.planmyplate.model.RecipeResponse

    @POST("api/ai/generate-meal-plan")
    suspend fun generateMealPlan(
        @Header("Authorization") token: String,
        @retrofit2.http.Query("userId") userId: Int,
        @retrofit2.http.Query("startDate") startDate: String? = null
    ): MealPlan
}
