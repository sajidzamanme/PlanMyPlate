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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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
        if (uiState.activeMealPlan != null && !uiState.isCreatingPlan) {
            WeeklyMealPlanView(
                mealPlan = uiState.activeMealPlan!!,
                modifier = Modifier.padding(padding),
                onDelete = { /* Implement delete/reset logic if needed */ },
                onCreateNew = { viewModel.startNewPlan() }
            )
        } else {
            CreateMealPlanContent(
                uiState = uiState,
                viewModel = viewModel,
                navController = navController,
                padding = padding,
                allRecipesSelected = allRecipesSelected,
                selectedMealType = selectedMealType,
                onMealTypeClick = { selectedMealType = it },
                onRecipeClick = { recipeToShowDetails = it }
            )
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
@Composable
fun CreateMealPlanContent(
    uiState: com.teamconfused.planmyplate.ui.viewmodels.MealPlanUiState,
    viewModel: MealPlanViewModel,
    navController: NavController,
    padding: PaddingValues,
    allRecipesSelected: Boolean,
    selectedMealType: String?,
    onMealTypeClick: (String) -> Unit,
    onRecipeClick: (Recipe) -> Unit
) {
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
            onClick = { onMealTypeClick("Breakfast") },
            onRecipeClick = onRecipeClick
        )
        
        MealTypeCard(
            mealType = "Lunch",
            selectedCount = uiState.selectedRecipes["Lunch"]?.size ?: 0,
            selectedRecipesList = uiState.selectedRecipes["Lunch"] ?: emptyList(),
            onClick = { onMealTypeClick("Lunch") },
            onRecipeClick = onRecipeClick
        )
        
        MealTypeCard(
            mealType = "Dinner",
            selectedCount = uiState.selectedRecipes["Dinner"]?.size ?: 0,
            selectedRecipesList = uiState.selectedRecipes["Dinner"] ?: emptyList(),
            onClick = { onMealTypeClick("Dinner") },
            onRecipeClick = onRecipeClick
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
                    // sessionManager set handled in ViewModel success mostly or here
                    // Assuming sessionManager access from Screen
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

@Composable
fun WeeklyMealPlanView(
    mealPlan: com.teamconfused.planmyplate.model.MealPlan,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    onCreateNew: () -> Unit
) {
    // Process slots to group by Date/Day
    val slots = mealPlan.slots ?: emptyList()
    val startDate = try {
        if (mealPlan.startDate != null) LocalDate.parse(mealPlan.startDate) else null
    } catch (e: Exception) { null }
    val today = LocalDate.now()

    // Grouping Logic
    // We want to map each slot to a standardized "Day Index" (1..7) relative to start date
    // Then we can display "Day X" or the actual Date.
    
    // Grouping Logic
    // We want to map each slot to a standardized "Day Index" (1..7) relative to start date
    // Then we can display "Day X" or the actual Date.
    
    val groupedByDayIndex = slots.mapIndexed { index, slot ->
        // Priority: 
        // 1. Explicit clean dayNumber/day (1-based)
        // 2. Date calculation
        // 3. List position inference (assuming ordered list from creation: 3 meals/day)
        
        val explicitDay = slot.dayNumber ?: slot.day
        
        val dayIndex = if (explicitDay != null && explicitDay > 0) {
            explicitDay
        } else {
             val dateDerived = if (slot.date != null && startDate != null) {
                 try {
                     ChronoUnit.DAYS.between(startDate, LocalDate.parse(slot.date)).toInt() + 1
                 } catch (e: Exception) { 0 }
             } else 0
             
             if (dateDerived > 0) {
                 dateDerived
             } else {
                 // Fallback: Infer from list index
                 // 0,1,2 -> Day 1; 3,4,5 -> Day 2; etc.
                 (index / 3) + 1
             }
        }
        dayIndex to slot
    }.groupBy { it.first }
     .mapValues { it.value.map { pair -> pair.second } }
     .toSortedMap()

    // Ensure we cover days 1..7 if slots are sparse? 
    // Or just show what we have. User requested "seven cards". 
    // Ideally we iterate 1..7 and find meals.
    
    val daysList = (1..7).toList()
    
    // Calculate which day index corresponds to "Today"
    val todayIndex = if (startDate != null) {
        ChronoUnit.DAYS.between(startDate, today).toInt() + 1
    } else {
        // Fallback: if any slot has today's date
        if (slots.any { it.date == today.toString() }) {
             // Find that slot's day index
             val todaySlot = slots.find { it.date == today.toString() }
             todaySlot?.dayNumber ?: todaySlot?.day ?: 0
        } else -1
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Your Weekly Plan",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        daysList.forEach { dayIndex ->
            val slotsForDay = groupedByDayIndex[dayIndex] ?: emptyList()
            
            // Determine date string
            val dateForDay = if (startDate != null) {
                startDate.plusDays(dayIndex.toLong() - 1)
            } else null
            
            val isToday = dayIndex == todayIndex || (dateForDay != null && dateForDay == today)
            
            val displayDate = dateForDay?.format(DateTimeFormatter.ofPattern("EEEE, MMM d")) ?: "Day $dayIndex"

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isToday) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isToday) 6.dp else 2.dp),
                border = if (isToday) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = displayDate,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        if (isToday) {
                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                Text("Today", modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                    }

                    HorizontalDivider(
                        color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outlineVariant
                    )

                    if (slotsForDay.isEmpty()) {
                        Text(
                            text = "No meals planned.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                             color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        // Sort meals: Breakfast, Lunch, Dinner
                        val sortedSlots = slotsForDay.sortedBy { 
                            when(it.mealType) {
                                "Breakfast" -> 1
                                "Lunch" -> 2
                                "Dinner" -> 3
                                else -> 4
                            }
                        }

                        sortedSlots.forEach { slot ->
                             Row(
                                 verticalAlignment = Alignment.CenterVertically,
                                 modifier = Modifier.fillMaxWidth() 
                             ) {
                                 Text(
                                     text = slot.mealType,
                                     style = MaterialTheme.typography.labelMedium,
                                     fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                     modifier = Modifier.width(80.dp),
                                     color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha=0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                 )
                                 slot.recipe?.let { recipe ->
                                     Text(
                                         text = recipe.name,
                                         style = MaterialTheme.typography.bodyMedium,
                                         color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                     )
                                 } ?: Text(
                                     text = "Recipe not found", 
                                     style = MaterialTheme.typography.bodySmall,
                                     fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                 )
                             }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = onCreateNew,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Create New Plan (Replace)")
        }
    }
}

