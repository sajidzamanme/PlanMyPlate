package com.teamconfused.planmyplate.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamconfused.planmyplate.domain.usecase.CreateMealPlanUseCase
import com.teamconfused.planmyplate.domain.usecase.FilterRecipesUseCase
import com.teamconfused.planmyplate.domain.usecase.GenerateRecipeUseCase
import com.teamconfused.planmyplate.domain.usecase.GetAllRecipesUseCase
import com.teamconfused.planmyplate.domain.usecase.GetTodaysMealsUseCase
import com.teamconfused.planmyplate.model.Recipe
import com.teamconfused.planmyplate.model.MealPlan
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
    val isLoadingHistory: Boolean = false,
    val activeMealPlan: MealPlan? = null,
    val mealPlans: List<MealPlan> = emptyList(),
    val additionalMeals: List<com.teamconfused.planmyplate.model.AdditionalMeal> = emptyList(),
    val handledMeals: Map<String, Set<String>> = emptyMap(),
    val errorMessage: String? = null
)

class MealPlanViewModel(
    private val createMealPlanUseCase: CreateMealPlanUseCase,
    private val getTodaysMealsUseCase: GetTodaysMealsUseCase, // Reuse for fetching weekly/active if needed, or use Repo directly.
    private val getAllRecipesUseCase: GetAllRecipesUseCase,
    private val filterRecipesUseCase: FilterRecipesUseCase,
    private val generateRecipeUseCase: GenerateRecipeUseCase, // For AI generation
    private val generateMealPlanUseCase: com.teamconfused.planmyplate.domain.usecase.GenerateMealPlanUseCase,
    private val mealPlanRepository: com.teamconfused.planmyplate.domain.repository.MealPlanRepository,
    private val sessionManager: com.teamconfused.planmyplate.util.SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealPlanUiState())
    val uiState: StateFlow<MealPlanUiState> = _uiState.asStateFlow()

    // Replicate Recipe Lists for selection UI
    private val _allRecipesState = MutableStateFlow<RecipeUiState>(RecipeUiState.Loading)
    val allRecipesState: StateFlow<RecipeUiState> = _allRecipesState.asStateFlow()

    private val _recommendedRecipesState = MutableStateFlow<RecipeUiState>(RecipeUiState.Loading)
    val recommendedRecipesState: StateFlow<RecipeUiState> = _recommendedRecipesState.asStateFlow()

    private val _budgetRecipesState = MutableStateFlow<RecipeUiState>(RecipeUiState.Loading)
    val budgetRecipesState: StateFlow<RecipeUiState> = _budgetRecipesState.asStateFlow()

    init {
        loadLocalData()
    }
    
    fun loadLocalData() {
        _uiState.update { 
            it.copy(
                additionalMeals = sessionManager.getAdditionalMeals(),
                handledMeals = sessionManager.getHandledMeals()
            )
        }
    }
    
    fun loadRecipes() {
        val token = sessionManager.getAuthToken()
        if (token == null) return

        val authHeader = "Bearer $token"

        viewModelScope.launch {
            _allRecipesState.value = RecipeUiState.Loading
            _recommendedRecipesState.value = RecipeUiState.Loading
            _budgetRecipesState.value = RecipeUiState.Loading
            try {
                val all = getAllRecipesUseCase(authHeader)
                _allRecipesState.value = RecipeUiState.Success(all)
                _recommendedRecipesState.value = RecipeUiState.Success(all.shuffled().take(5))
                _budgetRecipesState.value = RecipeUiState.Success(filterRecipesUseCase.byCalories(authHeader, 0, 400))
            } catch (e: Exception) {
                _allRecipesState.value = RecipeUiState.Error(e.message ?: "Failed to load recipes")
                _recommendedRecipesState.value = RecipeUiState.Error(e.message ?: "Failed to load recommended recipes")
                _budgetRecipesState.value = RecipeUiState.Error(e.message ?: "Failed to load budget recipes")
            }
        }
    }
    
    fun toggleRecipe(mealType: String, recipe: Recipe) {
        val current = _uiState.value.selectedRecipes[mealType] ?: emptyList()
        val updated = if (current.any { it.id == recipe.id }) {
            current.filter { it.id != recipe.id }
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
                     _uiState.update { it.copy(isCreatingPlan = false, errorMessage = "Error processing recipes.") }
                     return@launch
                }

                val token = sessionManager.getAuthToken() ?: ""
                val authHeader = "Bearer $token"
                createMealPlanUseCase(authHeader, userId, recipeIds)
                
                fetchWeeklyMealPlans()
                sessionManager.setHasMealPlans(true)

                _uiState.update { it.copy(isCreatingPlan = false, planCreated = true) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isCreatingPlan = false, errorMessage = e.message ?: "Failed to create meal plan") }
            }
        }
    }

    fun fetchWeeklyMealPlans() {
        val userId = sessionManager.getUserId()
        if (userId == -1) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHistory = true) }
            try {
                val token = sessionManager.getAuthToken() ?: ""
                val authHeader = "Bearer $token"
                val plans = mealPlanRepository.getWeeklyMealPlans(authHeader, userId)
                val active = plans.find { it.status.equals("active", ignoreCase = true) }
                _uiState.update {
                    it.copy(
                        isLoadingHistory = false,
                        mealPlans = plans,
                        activeMealPlan = active
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingHistory = false) }
            }
        }
    }

    fun retryFetchRecipes() {
        loadRecipes()
    }
    
    fun refreshRecipes() {
        loadRecipes()
    }

    fun startNewPlan() {
        _uiState.update {
            it.copy(
                activeMealPlan = null, // Logic to clear active plan locally or just reset UI mode
                isCreatingPlan = false,
                selectedRecipes = mapOf(
                    "Breakfast" to emptyList(),
                    "Lunch" to emptyList(),
                    "Dinner" to emptyList()
                )
            )
        }
    }
    
    fun generateMealPlan(onSuccess: () -> Unit) {
        val userId = sessionManager.getUserId()
        val token = sessionManager.getAuthToken()
        
        if (userId == -1 || token == null) {
            _uiState.update { it.copy(errorMessage = "User not logged in") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingPlan = true, errorMessage = null) }
            try {
                // Call AI UseCase
                generateMealPlanUseCase("Bearer $token", userId)
                
                fetchWeeklyMealPlans() // Refresh list
                sessionManager.setHasMealPlans(true)
                
                _uiState.update { it.copy(isCreatingPlan = false, planCreated = true) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isCreatingPlan = false, 
                        errorMessage = e.message ?: "Failed to generate meal plan"
                    ) 
                }
            }
        }
    }
}
