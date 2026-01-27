package com.teamconfused.planmyplate.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamconfused.planmyplate.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MealPlanUiState(
    val selectedRecipes: Map<String, List<Recipe>> = mapOf(
        "Breakfast" to emptyList(),
        "Lunch" to emptyList(),
        "Dinner" to emptyList()
    ),
    val isCreatingPlan: Boolean = false,
    val planCreated: Boolean = false,
    val errorMessage: String? = null
)

class MealPlanViewModel(
    private val recipeViewModel: RecipeViewModel,
    private val mealPlanService: com.teamconfused.planmyplate.network.MealPlanService,
    private val sessionManager: com.teamconfused.planmyplate.util.SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealPlanUiState())
    val uiState: StateFlow<MealPlanUiState> = _uiState.asStateFlow()

    // Expose recipe states from RecipeViewModel
    val recommendedRecipesState = recipeViewModel.recommendedRecipesState
    val budgetRecipesState = recipeViewModel.budgetRecipesState

    fun toggleRecipe(mealType: String, recipe: Recipe) {
        val current = _uiState.value.selectedRecipes[mealType] ?: emptyList()
        val updated = if (current.contains(recipe)) {
            current - recipe
        } else if (current.size < 7) {
            current + recipe
        } else {
            current
        }
        
        _uiState.value = _uiState.value.copy(
            selectedRecipes = _uiState.value.selectedRecipes + (mealType to updated)
        )
    }

    fun createMealPlan(onSuccess: () -> Unit) {
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            _uiState.update { it.copy(errorMessage = "User not logged in") }
            return
        }

        val allRecipesSelected = _uiState.value.selectedRecipes.values.all { it.size == 7 }
        if (!allRecipesSelected) {
            _uiState.update { it.copy(errorMessage = "Please select 7 recipes for each meal type") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingPlan = true, errorMessage = null) }
            try {
                // Flatten recipes in order: Day 1 Breakfast, Lunch, Dinner, Day 2 ...
                // The map is by meal type, each has 7 items.
                // We need to construct the list: [D1_B, D1_L, D1_D, D2_B, D2_L, D2_D, ...]
                
                val breakfast = _uiState.value.selectedRecipes["Breakfast"] ?: emptyList()
                val lunch = _uiState.value.selectedRecipes["Lunch"] ?: emptyList()
                val dinner = _uiState.value.selectedRecipes["Dinner"] ?: emptyList()

                val recipeIds = mutableListOf<Int>()
                
                for (i in 0 until 7) {
                    breakfast.getOrNull(i)?.id?.let { recipeIds.add(it) }
                    lunch.getOrNull(i)?.id?.let { recipeIds.add(it) }
                    dinner.getOrNull(i)?.id?.let { recipeIds.add(it) }
                }

                if (recipeIds.size != 21) {
                     _uiState.update { 
                         it.copy(
                             isCreatingPlan = false, 
                             errorMessage = "Error processing recipes. Please try again."
                         ) 
                     }
                     return@launch
                }

                // Calculate start date (e.g. tomorrow or next Monday)
                // For simplicity, using "2026-02-01" as placeholder or implement date logic
                // In a real app we'd ask user or pick next day.
                // Using a dynamic date based on current time for better simulation
                val startDate = java.time.LocalDate.now().plusDays(1).toString()

                val request = com.teamconfused.planmyplate.model.CreateMealPlanRequest(
                    recipeIds = recipeIds,
                    duration = 7,
                    startDate = startDate
                )

                mealPlanService.createMealPlanWithRecipes(userId, request)
                
                _uiState.update {
                    it.copy(
                        isCreatingPlan = false,
                        planCreated = true
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCreatingPlan = false,
                        errorMessage = e.message ?: "Failed to create meal plan"
                    )
                }
            }
        }
    }

    fun retryFetchRecipes() {
        recipeViewModel.fetchRecommendedRecipes()
        recipeViewModel.fetchBudgetRecipes()
    }
}
