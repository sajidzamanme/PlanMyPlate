package com.teamconfused.planmyplate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.teamconfused.planmyplate.model.Recipe
import com.teamconfused.planmyplate.ui.components.HorizontalRecipeCard
import com.teamconfused.planmyplate.ui.viewmodels.HomeViewModel
import com.teamconfused.planmyplate.ui.viewmodels.ViewModelFactory
import com.teamconfused.planmyplate.util.SessionManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val hasMealPlans = sessionManager.hasMealPlans()
    
    val viewModelFactory = ViewModelFactory(sessionManager)
    val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
    val uiState by homeViewModel.uiState.collectAsState()
    
    if (hasMealPlans) {
        DashboardWithMeals(navController, uiState, onRetry = { homeViewModel.retry() })
    } else {
        EmptyDashboard(navController)
    }
}

@Composable
fun DashboardWithMeals(
    navController: NavController,
    uiState: com.teamconfused.planmyplate.ui.viewmodels.HomeUiState,
    onRetry: () -> Unit
) {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d")
    
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    if (uiState.errorMessage != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error Loading Meals",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = uiState.errorMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            FilledTonalButton(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Retry")
            }
        }
        return
    }
    
    val todayCalories = uiState.todayCalories
    val weeklyCalories = 9800 // TODO: Calculate from actual weekly data
    val weeklyGoal = 14000
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = currentDate.format(formatter),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Today",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$todayCalories",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "calories",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "This Week",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$weeklyCalories",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "of $weeklyGoal goal",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
        
        Text(
            text = "Today's Meals",
            style = MaterialTheme.typography.headlineSmall
        )
        
        uiState.todayBreakfast?.let { 
            MealSection("Breakfast", listOf(it))
        }
        uiState.todayLunch?.let { 
            MealSection("Lunch", listOf(it))
        }
        uiState.todayDinner?.let { 
            MealSection("Dinner", listOf(it))
        }
        
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        )
        
        Text(
            text = "Upcoming Meals",
            style = MaterialTheme.typography.headlineSmall
        )
        
        uiState.tomorrowBreakfast?.let {
            UpcomingMealSection("Tomorrow - Breakfast", listOf(it))
        }
        uiState.tomorrowLunch?.let {
            UpcomingMealSection("Tomorrow - Lunch", listOf(it))
        }
        
        FilledTonalButton(
            onClick = { navController.navigate("meal_plan") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("View Full Meal Plan")
        }
    }
}

@Composable
fun MealSection(mealType: String, recipes: List<Recipe>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = mealType,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        recipes.forEach { recipe ->
            HorizontalRecipeCard(recipe = recipe, onClick = {})
        }
    }
}

@Composable
fun UpcomingMealSection(label: String, recipes: List<Recipe>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        recipes.forEach { recipe ->
            HorizontalRecipeCard(recipe = recipe, onClick = {})
        }
    }
}

@Composable
fun EmptyDashboard(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No Meal Plans Yet",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Create your first meal plan to see your daily meals here",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        FilledTonalButton(
            onClick = { navController.navigate("meal_plan") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Create Meal Plan")
        }
    }
}
