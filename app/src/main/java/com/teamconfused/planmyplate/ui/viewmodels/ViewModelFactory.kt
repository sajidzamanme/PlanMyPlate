package com.teamconfused.planmyplate.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teamconfused.planmyplate.network.RetrofitClient
import com.teamconfused.planmyplate.util.SessionManager

class ViewModelFactory(private val sessionManager: SessionManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(sessionManager) as T
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> SignupViewModel(sessionManager) as T
            modelClass.isAssignableFrom(PreferenceSelectionViewModel::class.java) -> PreferenceSelectionViewModel(sessionManager) as T
            modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java) -> ForgotPasswordViewModel() as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(sessionManager) as T
            modelClass.isAssignableFrom(RecipeViewModel::class.java) -> RecipeViewModel(RetrofitClient.recipeService) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(RetrofitClient.recipeService, RetrofitClient.mealPlanService, sessionManager) as T
            modelClass.isAssignableFrom(MealPlanViewModel::class.java) -> {
                val recipeViewModel = RecipeViewModel(RetrofitClient.recipeService)
                MealPlanViewModel(recipeViewModel, RetrofitClient.mealPlanService, sessionManager) as T
            }
            modelClass.isAssignableFrom(GroceryViewModel::class.java) -> GroceryViewModel(RetrofitClient.groceryListService, sessionManager) as T
            modelClass.isAssignableFrom(InventoryViewModel::class.java) -> InventoryViewModel(RetrofitClient.inventoryService, sessionManager) as T
            modelClass.isAssignableFrom(AddRecipeViewModel::class.java) -> AddRecipeViewModel(RetrofitClient.recipeService) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

