package com.teamconfused.planmyplate.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.teamconfused.planmyplate.ui.components.BottomNavigationBar
import com.teamconfused.planmyplate.ui.screens.MealPlanScreen
import com.teamconfused.planmyplate.ui.screens.GroceriesScreen
import com.teamconfused.planmyplate.ui.screens.HomeScreen
import com.teamconfused.planmyplate.ui.screens.SettingsScreen

@Composable
fun MainNav() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("meal_plan") { MealPlanScreen() }
            composable("groceries") { GroceriesScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}
