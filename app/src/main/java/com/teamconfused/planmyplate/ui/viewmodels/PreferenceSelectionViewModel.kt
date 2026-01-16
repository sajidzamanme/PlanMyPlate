package com.teamconfused.planmyplate.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamconfused.planmyplate.model.UserPreferencesDto
import com.teamconfused.planmyplate.network.RetrofitClient
import com.teamconfused.planmyplate.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PreferenceSelectionUiState(
    val currentStep: Int = 0,
    val selectedDiet: String? = null,
    val selectedAllergies: Set<String> = emptySet(),
    val selectedDislikes: Set<String> = emptySet(),
    val selectedServings: Int? = null,
    val selectedBudget: Float = 50F,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class PreferenceSelectionViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val _uiState = MutableStateFlow(PreferenceSelectionUiState())
    val uiState: StateFlow<PreferenceSelectionUiState> = _uiState.asStateFlow()

    fun onDietSelected(diet: String) {
        _uiState.update { it.copy(selectedDiet = diet) }
    }

    fun onAllergyToggled(allergy: String) {
        _uiState.update {
            val current = it.selectedAllergies
            if (current.contains(allergy)) {
                it.copy(selectedAllergies = current - allergy)
            } else {
                it.copy(selectedAllergies = current + allergy)
            }
        }
    }

    fun onDislikeToggled(dislike: String) {
        _uiState.update {
            val current = it.selectedDislikes
            if (current.contains(dislike)) {
                it.copy(selectedDislikes = current - dislike)
            } else {
                it.copy(selectedDislikes = current + dislike)
            }
        }
    }

    fun onServingsSelected(servings: Int) {
        _uiState.update { it.copy(selectedServings = servings) }
    }

    fun onBudgetSelected(budget: Float) {
        _uiState.update { it.copy(selectedBudget = budget) }
    }

    fun onNextStep(onComplete: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.currentStep < 4) {
            _uiState.update { it.copy(currentStep = it.currentStep + 1) }
        } else {
            savePreferences(onComplete)
        }
    }

    private fun savePreferences(onComplete: () -> Unit) {
        val currentState = _uiState.value
        val userId = sessionManager.getUserId()
        
        if (userId == -1) {
            _uiState.update { it.copy(errorMessage = "User not logged in") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Join the sets into comma-separated strings to match backend's expectation
                val allergiesString = if (currentState.selectedAllergies.isEmpty()) null 
                                     else currentState.selectedAllergies.joinToString(",")
                val dislikesString = if (currentState.selectedDislikes.isEmpty()) null 
                                    else currentState.selectedDislikes.joinToString(",")

                val dto = UserPreferencesDto(
                    diet = currentState.selectedDiet,
                    allergies = allergiesString,
                    dislikes = dislikesString,
                    servings = currentState.selectedServings,
                    budget = currentState.selectedBudget
                )
                RetrofitClient.userPreferencesService.setPreferences(userId, dto)
                _uiState.update { it.copy(isLoading = false) }
                onComplete()
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = e.localizedMessage ?: "Failed to save preferences"
                    ) 
                }
            }
        }
    }

    suspend fun isPreferencesSet(id: Int): Boolean {
        return try {
            val response = RetrofitClient.userPreferencesService.getPreferences(id)
            // Check if the returned DTO has actual data.
            // Adjust this condition based on how your backend indicates "no preferences"
            response != null && (response.diet != null || response.servings != null)
        } catch (e: Exception) {
            // If 404 is thrown, it usually means preferences don't exist
            false
        }
    }

    fun onPreviousStep(onBack: () -> Unit) {
        _uiState.update {
            if (it.currentStep > 0) {
                it.copy(currentStep = it.currentStep - 1)
            } else {
                onBack()
                it
            }
        }
    }
}
