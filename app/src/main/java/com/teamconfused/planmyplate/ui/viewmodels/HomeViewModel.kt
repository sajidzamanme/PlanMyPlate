package com.teamconfused.planmyplate.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamconfused.planmyplate.model.Recipe
import com.teamconfused.planmyplate.model.toRecipe
import com.teamconfused.planmyplate.network.RecipeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val todayBreakfast: Recipe? = null,
    val todayLunch: Recipe? = null,
    val todayDinner: Recipe? = null,
    val tomorrowBreakfast: Recipe? = null,
    val tomorrowLunch: Recipe? = null,
    val errorMessage: String? = null
) {
    val todayCalories: Int
        get() = (todayBreakfast?.calories ?: 0) + 
                (todayLunch?.calories ?: 0) + 
                (todayDinner?.calories ?: 0)
}

class HomeViewModel(
    private val recipeService: RecipeService,
    private val mealPlanService: com.teamconfused.planmyplate.network.MealPlanService,
    private val sessionManager: com.teamconfused.planmyplate.util.SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchTodaysMeals()
    }

    fun fetchTodaysMeals() {
        val userId = sessionManager.getUserId()
        if (userId == -1) {
             _uiState.update { it.copy(isLoading = false, errorMessage = "User not logged in") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Fetch weekly meal plans first
                val plans = mealPlanService.getWeeklyMealPlans(userId)
                
                // Find active plan
                val activePlan = plans.find { it.status == "active" }
                
                if (activePlan != null && !activePlan.meals.isNullOrEmpty()) {
                    // Logic to find "today's" meals. 
                    // Simplified: Assuming creation date represents Day 1.
                    // Real app should compare dates.
                    
                    // For demo/simplicity, let's just show Day 1's meals as Today 
                    // and Day 2 as Tomorrow.
                    // In a real scenario: calculate diff between LocalDate.now() and startDate.
                    
                    // Assuming Day 1
                    val day1Meals = activePlan.meals.filter { it.day == 1 }
                    val day2Meals = activePlan.meals.filter { it.day == 2 }

                    // Fetch recipe details if needed. Models.kt says MealPlanItem has optional recipe object.
                    // If backend populates it, great. If not, we might need to fetch by ID.
                    // Assuming backend populates 'recipe' in MealPlanItem or we use recipeService
                    
                    // Helper to exact recipe
                    suspend fun getRecipe(item: com.teamconfused.planmyplate.model.MealPlanItem): Recipe? {
                        return if (item.recipe != null) {
                            item.recipe.toRecipe()
                        } else {
                            try {
                                recipeService.getRecipeById(item.recipeId).toRecipe()
                            } catch (e: Exception) { null }
                        }
                    }

                    val todayBreakfast = day1Meals.find { it.mealType == "Breakfast" }?.let { getRecipe(it) }
                    val todayLunch = day1Meals.find { it.mealType == "Lunch" }?.let { getRecipe(it) }
                    val todayDinner = day1Meals.find { it.mealType == "Dinner" }?.let { getRecipe(it) }
                    
                    val tomorrowBreakfast = day2Meals.find { it.mealType == "Breakfast" }?.let { getRecipe(it) }
                     val tomorrowLunch = day2Meals.find { it.mealType == "Lunch" }?.let { getRecipe(it) }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            todayBreakfast = todayBreakfast,
                            todayLunch = todayLunch,
                            todayDinner = todayDinner,
                            tomorrowBreakfast = tomorrowBreakfast,
                            tomorrowLunch = tomorrowLunch
                        )
                    }
                } else {
                     _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null // empty dashboard is handled by UI
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to fetch meals"
                    )
                }
            }
        }
    }

    fun retry() {
        fetchTodaysMeals()
    }
}
