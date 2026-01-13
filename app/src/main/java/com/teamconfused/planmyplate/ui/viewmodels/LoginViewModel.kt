package com.teamconfused.planmyplate.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamconfused.planmyplate.model.SigninRequest
import com.teamconfused.planmyplate.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun onLoginClick(onLoginSuccess: () -> Unit) {
        val currentState = _uiState.value
        var isValid = true

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
        }

        if (isValid) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    val request = SigninRequest(
                        email = currentState.email,
                        password = currentState.password
                    )
                    val response = RetrofitClient.authService.signin(request)
                    // You might want to save the token from response.token here
                    _uiState.update { it.copy(isLoading = false) }
                    onLoginSuccess()
                } catch (e: Exception) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = e.localizedMessage ?: "Login failed. Please try again."
                        ) 
                    }
                }
            }
        }
    }
}
