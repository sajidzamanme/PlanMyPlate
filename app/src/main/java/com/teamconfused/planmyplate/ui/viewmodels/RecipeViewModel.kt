package com.teamconfused.planmyplate.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamconfused.planmyplate.domain.usecase.FilterRecipesUseCase
import com.teamconfused.planmyplate.domain.usecase.GetAllRecipesUseCase
import com.teamconfused.planmyplate.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RecipeUiState {
    object Loading : RecipeUiState()
    data class Success(val recipes: List<Recipe>) : RecipeUiState()
    data class Error(val message: String) : RecipeUiState()
}

class RecipeViewModel(
    private val getAllRecipesUseCase: GetAllRecipesUseCase,
    private val filterRecipesUseCase: FilterRecipesUseCase
) : ViewModel() {

    private val _allRecipesState = MutableStateFlow<RecipeUiState>(RecipeUiState.Loading)
    val allRecipesState: StateFlow<RecipeUiState> = _allRecipesState.asStateFlow()

    private val _recommendedRecipesState = MutableStateFlow<RecipeUiState>(RecipeUiState.Loading)
    val recommendedRecipesState: StateFlow<RecipeUiState> = _recommendedRecipesState.asStateFlow()

    private val _budgetRecipesState = MutableStateFlow<RecipeUiState>(RecipeUiState.Loading)
    val budgetRecipesState: StateFlow<RecipeUiState> = _budgetRecipesState.asStateFlow()

    init {
        fetchAllRecipes()
        fetchRecommendedRecipes()
        fetchBudgetRecipes()
    }

    fun fetchAllRecipes() {
        viewModelScope.launch {
            _allRecipesState.value = RecipeUiState.Loading
            try {
                val recipes = getAllRecipesUseCase()
                _allRecipesState.value = RecipeUiState.Success(recipes)
            } catch (e: Exception) {
                _allRecipesState.value = RecipeUiState.Error(e.message ?: "Failed to fetch recipes")
            }
        }
    }

    fun fetchRecommendedRecipes() {
        viewModelScope.launch {
            _recommendedRecipesState.value = RecipeUiState.Loading
            try {
                val all = getAllRecipesUseCase()
                // Simple recommendation logic: take 5 random
                _recommendedRecipesState.value = RecipeUiState.Success(all.shuffled().take(5))
            } catch (e: Exception) {
                 _recommendedRecipesState.value = RecipeUiState.Error(e.message ?: "Failed")
            }
        }
    }

    fun fetchBudgetRecipes() {
         viewModelScope.launch {
            _budgetRecipesState.value = RecipeUiState.Loading
            try {
                val budget = filterRecipesUseCase.byCalories(0, 400)
                _budgetRecipesState.value = RecipeUiState.Success(budget)
            } catch (e: Exception) {
                 _budgetRecipesState.value = RecipeUiState.Error(e.message ?: "Failed")
            }
        }
    }
    
    fun searchRecipes(query: String) {
        // Implement search using usage case or repository if needed
    }
}
