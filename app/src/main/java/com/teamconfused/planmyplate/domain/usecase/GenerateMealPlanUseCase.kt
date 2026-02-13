package com.teamconfused.planmyplate.domain.usecase

import com.teamconfused.planmyplate.domain.repository.AiRepository
import com.teamconfused.planmyplate.model.MealPlan

class GenerateMealPlanUseCase(
    private val aiRepository: AiRepository
) {
    suspend operator fun invoke(token: String, userId: Int, startDate: String? = null): MealPlan {
        return aiRepository.generateMealPlan(token, userId, startDate)
    }
}
