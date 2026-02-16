package com.teamconfused.planmyplate.data.repository

import android.util.Log
import com.teamconfused.planmyplate.domain.repository.AiRepository
import com.teamconfused.planmyplate.model.GenerateRecipeRequest
import com.teamconfused.planmyplate.model.MealPlan
import com.teamconfused.planmyplate.model.Recipe
import com.teamconfused.planmyplate.model.toRecipe
import com.teamconfused.planmyplate.network.AiService

class AiRepositoryImpl(
    private val api: AiService
) : AiRepository {
    private val TAG = "AiRepository"

    override suspend fun generateRecipe(token: String, request: GenerateRecipeRequest): Recipe {
        Log.d(TAG, "Requesting AI Recipe generation with: $request")
        return try {
            val response = api.generateRecipe(token, request)
            Log.d(TAG, "AI Recipe response received: $response")
            response.toRecipe()
        } catch (e: Exception) {
            Log.e(TAG, "AI Recipe generation failed: ${e.message}", e)
            throw e
        }
    }

    override suspend fun generateMealPlan(token: String, userId: Int, startDate: String?): MealPlan {
        Log.d(TAG, "Requesting AI Meal Plan for user $userId, startDate: $startDate")
        return try {
            val response = api.generateMealPlan(token, userId, startDate)
            Log.d(TAG, "AI Meal Plan response received: $response")
            response
        } catch (e: Exception) {
            Log.e(TAG, "AI Meal Plan generation failed: ${e.message}", e)
            throw e
        }
    }
}
