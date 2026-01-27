package com.teamconfused.planmyplate.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamconfused.planmyplate.model.UserPreferencesRequest
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
    val errorMessage: String? = null,
    val availableDiets: List<String> = emptyList(),
    val availableAllergies: List<String> = emptyList(),
    val availableDislikes: List<String> = emptyList()
)

class PreferenceSelectionViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val _uiState = MutableStateFlow(PreferenceSelectionUiState())
    val uiState: StateFlow<PreferenceSelectionUiState> = _uiState.asStateFlow()

    init {
        loadReferenceData()
    }

    private fun loadReferenceData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Parallel fetch
                val diets = RetrofitClient.userPreferencesService.getDiets().map { it.dietName }
                val allergies = RetrofitClient.userPreferencesService.getAllergies().map { it.allergyName }
                val dislikes = RetrofitClient.userPreferencesService.getDislikes().map { it.name }

                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        availableDiets = diets,
                        availableAllergies = allergies,
                        availableDislikes = dislikes
                    ) 
                }
            } catch (e: Exception) {
                 _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Failed to load options: ${e.localizedMessage}"
                    ) 
                }
            }
        }
    }

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
        
        // Admin bypass - skip server call
        if (userId == 0) {
            _uiState.update { it.copy(isLoading = false) }
            onComplete()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Convert sets to lists to match the backend's expectation
                val allergiesList = if (currentState.selectedAllergies.isEmpty()) null 
                                   else currentState.selectedAllergies.toList()
                val dislikesList = if (currentState.selectedDislikes.isEmpty()) null 
                                  else currentState.selectedDislikes.toList()

                val request = UserPreferencesRequest(
                    diet = currentState.selectedDiet,
                    allergies = allergiesList,
                    dislikes = dislikesList,
                    servings = currentState.selectedServings,
                    budget = currentState.selectedBudget
                )
                RetrofitClient.userPreferencesService.setPreferences(userId, request)
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
            // Check if the returned response has actual data.
            response.diet != null || response.servings != null
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
