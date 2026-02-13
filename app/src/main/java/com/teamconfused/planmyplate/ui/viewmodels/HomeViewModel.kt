package com.teamconfused.planmyplate.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamconfused.planmyplate.domain.usecase.GenerateRecipeUseCase
import com.teamconfused.planmyplate.domain.usecase.GetTodaysMealsUseCase
import com.teamconfused.planmyplate.model.Recipe
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
    val upcomingMeals: List<Recipe> = emptyList(),
    val upcomingDayLabel: String? = null,
    val upcomingMessage: String? = null,
    val errorMessage: String? = null
) {
    val todayCalories: Int
        get() = (todayBreakfast?.calories ?: 0) + 
                (todayLunch?.calories ?: 0) + 
                (todayDinner?.calories ?: 0)
}

class HomeViewModel(
    private val getTodaysMealsUseCase: GetTodaysMealsUseCase,
    private val generateRecipeUseCase: GenerateRecipeUseCase,
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
                // UseCase handles all logic
                val result = getTodaysMealsUseCase(userId)
                
                if (result.hasActivePlan) {
                     sessionManager.setHasMealPlans(true)
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        todayBreakfast = result.todayMeals.breakfast,
                        todayLunch = result.todayMeals.lunch,
                        todayDinner = result.todayMeals.dinner,
                        upcomingMeals = result.upcomingMeals,
                        upcomingDayLabel = result.upcomingDayLabel,
                        upcomingMessage = result.upcomingMessage
                    )
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

    // AI Generation
    private val _generatedRecipe = MutableStateFlow<Recipe?>(null)
    val generatedRecipe: StateFlow<Recipe?> = _generatedRecipe.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    fun generateRecipe(
        ingredients: List<String>,
        mealType: String,
        otherParams: Map<String, Any>
    ) {
        val userId = sessionManager.getUserId()
        if (userId == -1) return

        viewModelScope.launch {
            _isGenerating.value = true
            try {
                val token = sessionManager.getAuthToken() ?: ""
                
                val diet = sessionManager.getUserPreferences().diet
                val mood = otherParams["mood"] as? String

                 // UseCase
                val recipe = generateRecipeUseCase(
                    token = "Bearer $token",
                    ingredients = ingredients, 
                    mood = mood, 
                    dietaryPreference = diet, 
                    maxCalories = 800
                )
                _generatedRecipe.value = recipe
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun clearGeneratedRecipe() {
        _generatedRecipe.value = null
    }
}

