package com.teamconfused.planmyplate.domain.repository

import com.teamconfused.planmyplate.model.CreateMealPlanRequest
import com.teamconfused.planmyplate.model.MealPlan

interface MealPlanRepository {
    suspend fun getWeeklyMealPlans(userId: Int): List<MealPlan>
    suspend fun createMealPlanWithRecipes(userId: Int, request: CreateMealPlanRequest): MealPlan
}
