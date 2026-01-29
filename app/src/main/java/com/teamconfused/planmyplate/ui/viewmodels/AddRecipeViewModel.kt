package com.teamconfused.planmyplate.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamconfused.planmyplate.model.CreateRecipeRequest
import com.teamconfused.planmyplate.model.RecipeIngredientRequest
import com.teamconfused.planmyplate.network.RecipeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeIngredientInput(
    val ingId: Int = 0,
    val ingredientName: String = "",
    val quantity: String = "",
    val unit: String = ""
)

data class AddRecipeUiState(
    val name: String = "",
    val description: String = "",
    val calories: String = "",
    val prepTime: String = "",
    val cookTime: String = "",
    val servings: String = "",
    val instructions: String = "",
    val ingredients: List<RecipeIngredientInput> = listOf(RecipeIngredientInput()),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AddRecipeViewModel(
    private val recipeService: RecipeService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRecipeUiState())
    val uiState: StateFlow<AddRecipeUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateCalories(calories: String) {
        _uiState.update { it.copy(calories = calories) }
    }

    fun updatePrepTime(prepTime: String) {
        _uiState.update { it.copy(prepTime = prepTime) }
    }

    fun updateCookTime(cookTime: String) {
        _uiState.update { it.copy(cookTime = cookTime) }
    }

    fun updateServings(servings: String) {
        _uiState.update { it.copy(servings = servings) }
    }

    fun updateInstructions(instructions: String) {
        _uiState.update { it.copy(instructions = instructions) }
    }

    fun addIngredient() {
        _uiState.update {
            it.copy(ingredients = it.ingredients + RecipeIngredientInput())
        }
    }

    fun removeIngredient(index: Int) {
        _uiState.update {
            it.copy(ingredients = it.ingredients.filterIndexed { i, _ -> i != index })
        }
    }

    fun updateIngredient(index: Int, ingredient: RecipeIngredientInput) {
        _uiState.update {
            val updated = it.ingredients.toMutableList()
            if (index in updated.indices) {
                updated[index] = ingredient
            }
            it.copy(ingredients = updated)
        }
    }

    fun createRecipe(onSuccess: () -> Unit) {
        val state = _uiState.value

        // Validation
        if (state.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Recipe name is required") }
            return
        }

        val ingredientRequests = state.ingredients
            .filter { it.ingId > 0 && it.quantity.isNotBlank() }
            .map {
                RecipeIngredientRequest(
                    ingId = it.ingId,
                    quantity = it.quantity.toIntOrNull() ?: 1,
                    unit = it.unit.ifBlank { "unit" }
                )
            }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val request = CreateRecipeRequest(
                    name = state.name,
                    description = state.description.ifBlank { null },
                    calories = state.calories.toIntOrNull(),
                    prepTime = state.prepTime.toIntOrNull(),
                    cookTime = state.cookTime.toIntOrNull(),
                    servings = state.servings.toIntOrNull(),
                    instructions = state.instructions.ifBlank { null },
                    ingredients = ingredientRequests.ifEmpty { null }
                )

                recipeService.createRecipe(request)
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Recipe created successfully!"
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to create recipe: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
