package com.teamconfused.planmyplate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.teamconfused.planmyplate.ui.screens.RecipeSelectionScreen
import com.teamconfused.planmyplate.ui.screens.ForgotPasswordScreen
import com.teamconfused.planmyplate.ui.screens.LoginScreen
import com.teamconfused.planmyplate.ui.screens.PreferenceSelectionScreen
import com.teamconfused.planmyplate.ui.screens.RecipeDetailsScreen
import com.teamconfused.planmyplate.ui.screens.SignupScreen
import com.teamconfused.planmyplate.ui.screens.WelcomeScreen
import com.teamconfused.planmyplate.ui.viewmodels.ForgotPasswordViewModel
import com.teamconfused.planmyplate.ui.viewmodels.LoginViewModel
import com.teamconfused.planmyplate.ui.viewmodels.MealPlanViewModel
import com.teamconfused.planmyplate.ui.viewmodels.PreferenceSelectionViewModel
import com.teamconfused.planmyplate.ui.viewmodels.SignupViewModel
import com.teamconfused.planmyplate.util.SessionManager
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun NavGraph(navController: NavHostController) {
    val sessionManager: SessionManager = koinInject()
    
    val startDestination = if (sessionManager.isLoggedIn()) Screen.Main else Screen.Welcome

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Screen.Welcome> {
            WelcomeScreen(
                onGetStartedClick = { navController.navigate(Screen.Signup) },
                onLoginClick = { navController.navigate(Screen.Login) }
            )
        }

        composable<Screen.Login> {
            val viewModel: LoginViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()

            LoginScreen(
                uiState = uiState,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onLoginClick = {
                    viewModel.onLoginClick { hasPreferences ->
                        if (hasPreferences) {
                            navController.navigate(Screen.Main) {
                                popUpTo(Screen.Welcome) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.PreferenceSelection) {
                                popUpTo(Screen.Welcome) { inclusive = true }
                            }
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

        composable<Screen.Main> {
            MainNav(
                rootNavController = navController,
                onLogout = {
                    navController.navigate(Screen.Welcome) {
                        popUpTo(Screen.Main) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.RecipeDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.RecipeDetails>()
            
            val mainBackStackEntry = remember(backStackEntry) {
                navController.getStackEntry(Screen.Main) ?: backStackEntry
            }
            val mealPlanViewModel: MealPlanViewModel = koinViewModel(viewModelStoreOwner = mainBackStackEntry)
            val homeViewModel: com.teamconfused.planmyplate.ui.viewmodels.HomeViewModel = koinViewModel(viewModelStoreOwner = mainBackStackEntry)
            val uiState by mealPlanViewModel.uiState.collectAsState()
            
            val currentMealType = route.mealType ?: "Breakfast"
            val isAdded = if (route.isSelectionMode) {
                uiState.selectedRecipes[currentMealType]?.any { it.recipeId == route.recipeId } == true
            } else {
                true 
            }

            RecipeDetailsScreen(
                navController = navController,
                recipeId = route.recipeId,
                isInitiallyAdded = isAdded,
                showControls = !route.readOnly,
                fromDashboard = route.fromDashboard,
                mealType = route.mealType,
                onToggleRecipe = { recipe ->
                    if (route.isSelectionMode) {
                        mealPlanViewModel.toggleRecipe(currentMealType, recipe)
                    }
                },
                onCooked = { type, calories, id ->
                    homeViewModel.markAsCooked(type, calories, id)
                },
                onSkip = { id ->
                    homeViewModel.skipMeal(route.mealType, id)
                }
            )
        }

        composable<Screen.RecipeSelection> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.RecipeSelection>()
            
            val mainBackStackEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Main) ?: backStackEntry
            }
            val mealPlanViewModel: MealPlanViewModel = koinViewModel(viewModelStoreOwner = mainBackStackEntry)
            
            RecipeSelectionScreen(
                mealType = route.mealType,
                viewModel = mealPlanViewModel,
                onBackClick = { navController.popBackStack() },
                onRecipeDetailsClick = { recipeId ->
                    navController.navigate(
                        Screen.RecipeDetails(
                            recipeId = recipeId,
                            isSelectionMode = true,
                            mealType = route.mealType
                        )
                    )
                }
            )
        }

        composable<Screen.Signup> {
            val viewModel: SignupViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsState()

            SignupScreen(
                uiState = uiState,
                onFirstNameChange = viewModel::onFirstNameChange,
                onLastNameChange = viewModel::onLastNameChange,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onPhoneChange = viewModel::onPhoneChange,
                onDateOfBirthChange = viewModel::onDateOfBirthChange,
                onTermsAcceptedChange = viewModel::onTermsAcceptedChange,
                onLoginClick = {
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Welcome) { inclusive = false }
                    }
                },
                onSignupClick = {
                    viewModel.onSignupClick {
                        navController.navigate(Screen.PreferenceSelection) {
                            popUpTo(Screen.Welcome) { inclusive = true }
                        }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<Screen.ForgotPassword> {
            val viewModel: ForgotPasswordViewModel = koinViewModel()
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
            val viewModel: PreferenceSelectionViewModel = koinViewModel()
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
                        navController.navigate(Screen.Main) {
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

private fun NavHostController.getStackEntry(route: Any): androidx.navigation.NavBackStackEntry? {
    return try {
        getBackStackEntry(route)
    } catch (e: Exception) {
        null
    }
}
