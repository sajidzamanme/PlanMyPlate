package com.teamconfused.planmyplate.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ForgotPasswordStep {
    EMAIL_INPUT,
    VERIFICATION_CODE,
    RESET_PASSWORD,
    SUCCESS
}

data class ForgotPasswordUiState(
    val step: ForgotPasswordStep = ForgotPasswordStep.EMAIL_INPUT,
    val email: String = "",
    val code: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val error: String? = null
)

class ForgotPasswordViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onCodeChange(code: String) {
        if (code.length <= 4 && code.all { it.isDigit() }) {
             _uiState.update { it.copy(code = code, error = null) }
        }
    }

    fun onNewPasswordChange(password: String) {
        _uiState.update { it.copy(newPassword = password, error = null) }
    }

    fun onConfirmPasswordChange(password: String) {
        _uiState.update { it.copy(confirmPassword = password, error = null) }
    }

    fun onSendCodeClick() {
        val currentState = _uiState.value
        if (currentState.email.isBlank()) {
            _uiState.update { it.copy(error = "Email is required") }
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _uiState.update { it.copy(error = "Invalid email format") }
            return
        }
        
        // Simulate sending code...
        _uiState.update { it.copy(step = ForgotPasswordStep.VERIFICATION_CODE, error = null) }
    }

    fun onVerifyCodeClick() {
        val currentState = _uiState.value
        if (currentState.code.length != 4) {
            _uiState.update { it.copy(error = "Please enter a 4-digit code") }
            return
        }
        
        // Mock validation: "0000" is correct
        if (currentState.code == "0000") {
            _uiState.update { it.copy(step = ForgotPasswordStep.RESET_PASSWORD, error = null) }
        } else {
            _uiState.update { it.copy(error = "Invalid verification code") }
        }
    }

    fun onResetPasswordClick() {
        val currentState = _uiState.value
        if (currentState.newPassword.isBlank()) {
            _uiState.update { it.copy(error = "Password is required") }
            return
        }
        if (currentState.newPassword.length < 6) {
             _uiState.update { it.copy(error = "Password must be at least 6 characters") }
             return
        }
        if (currentState.newPassword != currentState.confirmPassword) {
            _uiState.update { it.copy(error = "Passwords do not match") }
            return
        }

        // Simulate password reset...
        _uiState.update { it.copy(step = ForgotPasswordStep.SUCCESS, error = null) }
    }
}
