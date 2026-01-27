package com.teamconfused.planmyplate.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamconfused.planmyplate.model.SignupRequest
import com.teamconfused.planmyplate.network.RetrofitClient
import com.teamconfused.planmyplate.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignupUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val isTermsAccepted: Boolean = false,
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val termsError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SignupViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(fullName = name, fullNameError = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun onTermsAcceptedChange(accepted: Boolean) {
        _uiState.update { it.copy(isTermsAccepted = accepted, termsError = null) }
    }

    fun onSignupClick(onSignupSuccess: () -> Unit) {
        val currentState = _uiState.value
        var isValid = true

        if (currentState.fullName.isBlank()) {
            _uiState.update { it.copy(fullNameError = "Full Name is required") }
            isValid = false
        }

        if (currentState.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email is required") }
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _uiState.update { it.copy(emailError = "Invalid email format") }
            isValid = false
        }

        if (currentState.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Password is required") }
            isValid = false
        } else if (currentState.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            isValid = false
        }

        if (!currentState.isTermsAccepted) {
            _uiState.update { it.copy(termsError = "You must accept the terms") }
            isValid = false
        }

        if (isValid) {
            // Admin bypass
            if (currentState.fullName == "admin" && currentState.email == "admin@email.com" && currentState.password == "12345678") {
                sessionManager.saveUserId(0)
                _uiState.update { it.copy(isLoading = false) }
                onSignupSuccess()
                return
            }
            
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    val request = SignupRequest(
                        name = currentState.fullName,
                        email = currentState.email,
                        password = currentState.password
                    )
                    val response = RetrofitClient.authService.signup(request)
                    val userId = response.getEffectiveUserId()
                    if (userId != null) {
                        sessionManager.saveUserId(userId)
                    } else {
                        android.util.Log.e("SignupViewModel", "Signup successful but no userId found in response: $response")
                    }
                    _uiState.update { it.copy(isLoading = false) }
                    onSignupSuccess()
                } catch (e: Exception) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = e.localizedMessage ?: "Signup failed. Please try again."
                        ) 
                    }
                }
            }
        }
    }
}
