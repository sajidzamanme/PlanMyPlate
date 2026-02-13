package com.teamconfused.planmyplate.domain.usecase

import com.teamconfused.planmyplate.domain.repository.MealPlanRepository
import com.teamconfused.planmyplate.model.CreateMealPlanRequest
import com.teamconfused.planmyplate.model.MealPlan
import java.time.LocalDate

class CreateMealPlanUseCase(
    private val repository: MealPlanRepository
) {
    suspend operator fun invoke(userId: Int, recipeIds: List<Int>, duration: Int = 7): MealPlan {
        // Calculate start date (e.g. tomorrow)
        val startDate = LocalDate.now().plusDays(1).toString()
        
        val request = CreateMealPlanRequest(
            recipeIds = recipeIds,
            duration = duration,
            startDate = startDate
        )

        return repository.createMealPlanWithRecipes(userId, request)
    }
}
