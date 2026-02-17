package com.teamconfused.planmyplate.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavController
import com.teamconfused.planmyplate.ui.components.BottomNavigationBar
import com.teamconfused.planmyplate.ui.screens.AddRecipeScreen
import com.teamconfused.planmyplate.ui.screens.MealPlanScreen
import com.teamconfused.planmyplate.ui.screens.GroceriesScreen
import com.teamconfused.planmyplate.ui.screens.HomeScreen
import com.teamconfused.planmyplate.ui.screens.InventoryScreen
import com.teamconfused.planmyplate.ui.screens.SettingsScreen
import com.teamconfused.planmyplate.ui.viewmodels.AddRecipeViewModel
import com.teamconfused.planmyplate.ui.viewmodels.SettingsViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.teamconfused.planmyplate.ui.viewmodels.MealPlanViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainNav(rootNavController: NavController, onLogout: () -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController, rootNavController) }
            composable("meal_plan") { MealPlanScreen(navController, rootNavController) }
            composable("groceries") { GroceriesScreen(navController) }
            composable("inventory") { InventoryScreen(navController) }
            composable("settings") {
                val settingsViewModel: SettingsViewModel = koinViewModel()
                SettingsScreen(
                    onLogoutClick = {
                        settingsViewModel.logout()
                        onLogout()
                    },
                    onUpdatePreferencesClick = { rootNavController.navigate(com.teamconfused.planmyplate.ui.navigation.Screen.PreferenceSelection) }
                )
            }
        }
    }
}
