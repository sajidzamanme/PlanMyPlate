package com.teamconfused.planmyplate.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teamconfused.planmyplate.util.SessionManager

class ViewModelFactory(private val sessionManager: SessionManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(sessionManager) as T
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> SignupViewModel(sessionManager) as T
            modelClass.isAssignableFrom(PreferenceSelectionViewModel::class.java) -> PreferenceSelectionViewModel(sessionManager) as T
            modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java) -> ForgotPasswordViewModel() as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
