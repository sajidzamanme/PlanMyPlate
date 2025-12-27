package com.teamconfused.planmyplate.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PreferenceSelectionUiState(
    val currentStep: Int = 0,
    val selectedDiet: String? = null,
    val selectedAllergies: Set<String> = emptySet(),
    val selectedDislikes: Set<String> = emptySet(),
    val selectedServings: Int? = null,
    val selectedBudget: Float = 50F
)

class PreferenceSelectionViewModel : ViewModel() {
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
        _uiState.update {
            if (it.currentStep < 4) {
                it.copy(currentStep = it.currentStep + 1)
            } else {
                onComplete()
                it
            }
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
