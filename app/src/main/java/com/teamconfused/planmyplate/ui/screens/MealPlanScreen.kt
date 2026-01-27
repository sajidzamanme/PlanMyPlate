package com.teamconfused.planmyplate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.teamconfused.planmyplate.model.Recipe
import com.teamconfused.planmyplate.ui.components.CategorizedRecipeSection
import com.teamconfused.planmyplate.ui.components.HorizontalRecipeCard
import com.teamconfused.planmyplate.ui.components.RecipeDetailsDialog
import com.teamconfused.planmyplate.ui.viewmodels.MealPlanViewModel
import com.teamconfused.planmyplate.ui.viewmodels.ViewModelFactory
import com.teamconfused.planmyplate.util.SessionManager
import com.teamconfused.planmyplate.R

@Composable
fun MealPlanScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    
    val viewModelFactory = ViewModelFactory(sessionManager)
    val viewModel: MealPlanViewModel = viewModel(factory = viewModelFactory)
    val uiState by viewModel.uiState.collectAsState()
    
    var selectedMealType by remember { mutableStateOf<String?>(null) }
    var recipeToShowDetails by remember { mutableStateOf<Recipe?>(null) }
    
    val allRecipesSelected = uiState.selectedRecipes.values.all { it.size == 7 }
    
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Create Weekly Meal Plan",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Text(
                text = "Select 7 recipes for each meal type",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            MealTypeCard(
                mealType = "Breakfast",
                selectedCount = uiState.selectedRecipes["Breakfast"]?.size ?: 0,
                selectedRecipesList = uiState.selectedRecipes["Breakfast"] ?: emptyList(),
                onClick = { selectedMealType = "Breakfast" },
                onRecipeClick = { recipeToShowDetails = it }
            )
            
            MealTypeCard(
                mealType = "Lunch",
                selectedCount = uiState.selectedRecipes["Lunch"]?.size ?: 0,
                selectedRecipesList = uiState.selectedRecipes["Lunch"] ?: emptyList(),
                onClick = { selectedMealType = "Lunch" },
                onRecipeClick = { recipeToShowDetails = it }
            )
            
            MealTypeCard(
                mealType = "Dinner",
                selectedCount = uiState.selectedRecipes["Dinner"]?.size ?: 0,
                selectedRecipesList = uiState.selectedRecipes["Dinner"] ?: emptyList(),
                onClick = { selectedMealType = "Dinner" },
                onRecipeClick = { recipeToShowDetails = it }
            )
            
            uiState.errorMessage?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            Button(
                onClick = {
                    viewModel.createMealPlan {
                        sessionManager.setHasMealPlans(true)
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = allRecipesSelected && !uiState.isCreatingPlan
            ) {
                if (uiState.isCreatingPlan) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create Meal Plan")
                }
            }
        }
    }
    
    selectedMealType?.let { mealType ->
        RecipeSelectionDialog(
            mealType = mealType,
            selectedRecipes = uiState.selectedRecipes[mealType] ?: emptyList(),
            onDismiss = { selectedMealType = null },
            onRecipeToggle = { recipe ->
                viewModel.toggleRecipe(mealType, recipe)
            },
            onRecipeLongClick = { recipe ->
                recipeToShowDetails = recipe
            },
            viewModel = viewModel
        )
    }
    
    recipeToShowDetails?.let { recipe ->
        val currentMealType = selectedMealType ?: "Breakfast"
        val isAdded = uiState.selectedRecipes[currentMealType]?.contains(recipe) == true
        RecipeDetailsDialog(
            recipe = recipe,
            isAdded = isAdded,
            onDismiss = { recipeToShowDetails = null },
            onToggleRecipe = {
                viewModel.toggleRecipe(currentMealType, recipe)
                recipeToShowDetails = null
            }
        )
    }
}

@Composable
fun MealTypeCard(mealType: String, selectedCount: Int, selectedRecipesList: List<Recipe>, onClick: () -> Unit, onRecipeClick: (Recipe) -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selectedCount == 7) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = mealType,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "$selectedCount/7 recipes selected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.add_icon),
                    contentDescription = "Select $mealType recipes",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            if (selectedRecipesList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                selectedRecipesList.forEach { recipe ->
                    HorizontalRecipeCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe) },
                        onLongClick = { onRecipeClick(recipe) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeSelectionDialog(
    mealType: String,
    selectedRecipes: List<Recipe>,
    onDismiss: () -> Unit,
    onRecipeToggle: (Recipe) -> Unit,
    onRecipeLongClick: (Recipe) -> Unit,
    viewModel: MealPlanViewModel
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val recommendedState by viewModel.recommendedRecipesState.collectAsState()
    val budgetState by viewModel.budgetRecipesState.collectAsState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Select $mealType Recipes",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Text(
                text = "${selectedRecipes.size}/7 selected",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Recommended Recipes Section
            when (recommendedState) {
                is com.teamconfused.planmyplate.ui.viewmodels.RecipeUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is com.teamconfused.planmyplate.ui.viewmodels.RecipeUiState.Success -> {
                    val recipes = (recommendedState as com.teamconfused.planmyplate.ui.viewmodels.RecipeUiState.Success).recipes
                    if (recipes.isNotEmpty()) {
                        CategorizedRecipeSection(
                            title = "Recommended",
                            recipes = recipes,
                            onRecipeClick = { onRecipeToggle(it) },
                            onRecipeLongClick = { onRecipeLongClick(it) },
                            onSeeAllClick = { },
                            selectedRecipes = selectedRecipes
                        )
                    }
                }
                is com.teamconfused.planmyplate.ui.viewmodels.RecipeUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Failed to load recommended recipes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        TextButton(onClick = { viewModel.retryFetchRecipes() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            
            // Budget Recipes Section
            when (budgetState) {
                is com.teamconfused.planmyplate.ui.viewmodels.RecipeUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is com.teamconfused.planmyplate.ui.viewmodels.RecipeUiState.Success -> {
                    val recipes = (budgetState as com.teamconfused.planmyplate.ui.viewmodels.RecipeUiState.Success).recipes
                    if (recipes.isNotEmpty()) {
                        CategorizedRecipeSection(
                            title = "Budget Options",
                            recipes = recipes,
                            onRecipeClick = { onRecipeToggle(it) },
                            onRecipeLongClick = { onRecipeLongClick(it) },
                            onSeeAllClick = { },
                            selectedRecipes = selectedRecipes
                        )
                    }
                }
                is com.teamconfused.planmyplate.ui.viewmodels.RecipeUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Failed to load budget recipes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        TextButton(onClick = { viewModel.retryFetchRecipes() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}
