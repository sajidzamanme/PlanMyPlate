package com.teamconfused.planmyplate.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.teamconfused.planmyplate.ui.screens.ForgotPasswordScreen
import com.teamconfused.planmyplate.ui.screens.LoginScreen
import com.teamconfused.planmyplate.ui.screens.PreferenceSelectionScreen
import com.teamconfused.planmyplate.ui.screens.SignupScreen
import com.teamconfused.planmyplate.ui.screens.WelcomeScreen
import com.teamconfused.planmyplate.ui.viewmodels.ForgotPasswordViewModel
import com.teamconfused.planmyplate.ui.viewmodels.LoginViewModel
import com.teamconfused.planmyplate.ui.viewmodels.PreferenceSelectionViewModel
import com.teamconfused.planmyplate.ui.viewmodels.SignupViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome
    ) {
        composable<Screen.Welcome> {
            WelcomeScreen(
                onGetStartedClick = { navController.navigate(Screen.Signup) },
                onLoginClick = { navController.navigate(Screen.Login) }
            )
        }

        composable<Screen.Login> {
            val viewModel: LoginViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            LoginScreen(
                uiState = uiState,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onLoginClick = {
                    viewModel.onLoginClick {
                        navController.navigate(Screen.PreferenceSelection) {
                            popUpTo(Screen.Welcome) { inclusive = true }
                        }
                    }
                },
                onSignupClick = {
                    navController.navigate(Screen.Signup) {
                        popUpTo(Screen.Welcome) { inclusive = false }
                    }
                },
                onBackClick = { navController.popBackStack() },
                onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword) }
            )
        }

        composable<Screen.Home> {
            // Placeholder for Home Screen
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Home Screen")
            }
        }

        composable<Screen.Signup> {
            val viewModel: SignupViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            SignupScreen(
                uiState = uiState,
                onFullNameChange = viewModel::onFullNameChange,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onTermsAcceptedChange = viewModel::onTermsAcceptedChange,
                onLoginClick = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Welcome) { inclusive = false }
                    }
                },
                onSignupClick = {
                    viewModel.onSignupClick {
                        // Navigate to PreferenceSelection flow after successful signup
                        navController.navigate(Screen.PreferenceSelection) {
                            popUpTo(Screen.Welcome) { inclusive = true }
                        }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Screen.ForgotPassword> {
            val viewModel: ForgotPasswordViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            ForgotPasswordScreen(
                uiState = uiState,
                onEmailChange = viewModel::onEmailChange,
                onCodeChange = viewModel::onCodeChange,
                onNewPasswordChange = viewModel::onNewPasswordChange,
                onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                onSendCodeClick = viewModel::onSendCodeClick,
                onVerifyCodeClick = viewModel::onVerifyCodeClick,
                onResetPasswordClick = viewModel::onResetPasswordClick,
                onLoginClick = {
                    navController.navigate(Screen.Login) {
                         popUpTo(Screen.Welcome) { inclusive = false }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Screen.PreferenceSelection> {
            val viewModel: PreferenceSelectionViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            PreferenceSelectionScreen(
                uiState = uiState,
                onDietSelected = viewModel::onDietSelected,
                onAllergyToggled = viewModel::onAllergyToggled,
                onDislikeToggled = viewModel::onDislikeToggled,
                onServingsSelected = viewModel::onServingsSelected,
                onBudgetSelected = viewModel::onBudgetSelected,
                onNextStep = {
                    viewModel.onNextStep {
                        navController.navigate(Screen.Home) {
                            popUpTo(Screen.PreferenceSelection) { inclusive = true }
                        }
                    }
                },
                onBackClick = {
                    viewModel.onPreviousStep {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}
