package com.teamconfused.planmyplate.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamconfused.planmyplate.model.Recipe
import com.teamconfused.planmyplate.model.toRecipe
import com.teamconfused.planmyplate.network.RecipeService
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
    private val recipeService: RecipeService
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
                val response = recipeService.getAllRecipes()
                val recipes = response.map { it.toRecipe() }
                _allRecipesState.value = RecipeUiState.Success(recipes)
            } catch (e: Exception) {
                _allRecipesState.value = RecipeUiState.Error(
                    e.message ?: "Failed to fetch recipes"
                )
            }
        }
    }

    fun fetchRecommendedRecipes() {
        viewModelScope.launch {
            _recommendedRecipesState.value = RecipeUiState.Loading
            try {
                // Fetch recipes with moderate to high calories (400-600) as "recommended"
                val response = recipeService.filterRecipesByCalories(
                    minCalories = 400,
                    maxCalories = 600
                )
                val recipes = response.map { it.toRecipe() }
                _recommendedRecipesState.value = RecipeUiState.Success(recipes)
            } catch (e: Exception) {
                _recommendedRecipesState.value = RecipeUiState.Error(
                    e.message ?: "Failed to fetch recommended recipes"
                )
            }
        }
    }

    fun fetchBudgetRecipes() {
        viewModelScope.launch {
            _budgetRecipesState.value = RecipeUiState.Loading
            try {
                // Fetch recipes with lower calories (200-400) as "budget friendly"
                val response = recipeService.filterRecipesByCalories(
                    minCalories = 200,
                    maxCalories = 400
                )
                val recipes = response.map { it.toRecipe() }
                _budgetRecipesState.value = RecipeUiState.Success(recipes)
            } catch (e: Exception) {
                _budgetRecipesState.value = RecipeUiState.Error(
                    e.message ?: "Failed to fetch budget recipes"
                )
            }
        }
    }

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            _allRecipesState.value = RecipeUiState.Loading
            try {
                val response = recipeService.searchRecipesByName(query)
                val recipes = response.map { it.toRecipe() }
                _allRecipesState.value = RecipeUiState.Success(recipes)
            } catch (e: Exception) {
                _allRecipesState.value = RecipeUiState.Error(
                    e.message ?: "Failed to search recipes"
                )
            }
        }
    }
}
