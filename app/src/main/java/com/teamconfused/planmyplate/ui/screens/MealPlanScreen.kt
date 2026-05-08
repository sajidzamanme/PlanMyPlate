package com.teamconfused.planmyplate.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.teamconfused.planmyplate.domain.model.Recipe
import com.teamconfused.planmyplate.domain.model.MealPlan
import com.teamconfused.planmyplate.domain.model.AdditionalMeal
import com.teamconfused.planmyplate.ui.components.CategorizedRecipeSection
import com.teamconfused.planmyplate.ui.components.HorizontalRecipeCard
import com.teamconfused.planmyplate.ui.viewmodels.MealPlanViewModel
import com.teamconfused.planmyplate.R
import com.teamconfused.planmyplate.ui.navigation.Screen
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.koin.androidx.compose.koinViewModel

@Composable
fun MealPlanScreen(
    navController: NavController, 
    rootNavController: NavController,
    viewModel: MealPlanViewModel = koinViewModel()
) {
    val context = LocalContext.current
    
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Refresh data whenever the screen becomes visible (ON_START)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.refreshAll()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    var selectedMealType by remember { mutableStateOf<String?>(null) }
    var recipeToShowDetails by remember { mutableStateOf<Recipe?>(null) }
    
    val allRecipesSelected = uiState.selectedRecipes.values.all { it.size == 7 }
    
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        if (uiState.activeMealPlan != null && !uiState.isCreatingPlan) {
            WeeklyMealPlanView(
                mealPlan = uiState.activeMealPlan!!,
                additionalMeals = uiState.additionalMeals,
                handledMeals = uiState.handledMeals,
                modifier = Modifier.padding(padding),
                onDelete = { /* Implement delete/reset logic if needed */ },
                onCreateNew = { viewModel.startNewPlan() },
                onRecipeClick = { recipeId ->
                    rootNavController.navigate(Screen.RecipeDetails(recipeId, readOnly = true))
                }
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
        LaunchedEffect(recipe.id) {
            rootNavController.navigate(
                Screen.RecipeDetails(
                    recipeId = recipe.id ?: 0,
                    isSelectionMode = true,
                    mealType = currentMealType
                )
            )
            recipeToShowDetails = null
        }
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
    val allRecipesState by viewModel.allRecipesState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.refreshRecipes()
    }
    
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

            when (allRecipesState) {
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
                    val recipes = (allRecipesState as com.teamconfused.planmyplate.ui.viewmodels.RecipeUiState.Success).recipes
                    if (recipes.isNotEmpty()) {
                        CategorizedRecipeSection(
                            title = "All Recipes",
                            recipes = recipes,
                            onRecipeClick = { onRecipeToggle(it) },
                            onRecipeLongClick = { onRecipeLongClick(it) },
                            onSeeAllClick = { },
                            selectedRecipes = selectedRecipes
                        )
                    } else {
                        Text(
                            text = "No recipes found. Try adding some in Settings!",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
                is com.teamconfused.planmyplate.ui.viewmodels.RecipeUiState.Error -> {
                    Text(
                        text = "Failed to load all recipes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
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
            text = "Select 7 recipes for each meal slot",
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
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    viewModel.generateMealPlan {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isCreatingPlan,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Auto Generate",
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1
                )
            }

            Button(
                onClick = {
                    viewModel.createMealPlan {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = allRecipesSelected && !uiState.isCreatingPlan,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text(
                    text = "Create Manual",
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1
                )
            }
        }
    }
    
    if (uiState.isCreatingPlan) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Creating Your Plan") },
            text = { 
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Our AI is crafting a balanced meal plan just for you...")
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun WeeklyMealPlanView(
    mealPlan: MealPlan,
    additionalMeals: List<AdditionalMeal> = emptyList(),
    handledMeals: Map<String, Set<String>> = emptyMap(),
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    onCreateNew: () -> Unit,
    onRecipeClick: (Int) -> Unit = {}
) {
    val slots = mealPlan.slots ?: emptyList()
    val startDate = try {
        if (mealPlan.startDate != null) LocalDate.parse(mealPlan.startDate) else null
    } catch (e: Exception) { null }
    val today = LocalDate.now()

    val groupedByDayIndex = slots.mapIndexed { index, slot ->
        val explicitDay = slot.dayNumber
        
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
                 (index / 3) + 1
             }
        }
        dayIndex to slot
    }.groupBy { it.first }
     .mapValues { it.value.map { pair -> pair.second } }
     .toSortedMap()

    val daysList = (1..7).toList()
    
    val todayIndex = if (startDate != null) {
        ChronoUnit.DAYS.between(startDate, today).toInt() + 1
    } else {
        if (slots.any { it.date == today.toString() }) {
             val todaySlot = slots.find { it.date == today.toString() }
             todaySlot?.dayNumber ?: 0
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
                        val mealTypes = listOf("Breakfast", "Lunch", "Dinner")
                        
                        mealTypes.forEach { type ->
                             val slot = slotsForDay.find { it.mealType.equals(type, ignoreCase = true) }
                             val additionalForSlot = additionalMeals.filter { 
                                 it.date == dateForDay.toString() && it.mealType.equals(type, ignoreCase = true)
                             }
                             val isHandled = handledMeals[dateForDay.toString()]?.contains(type) ?: false

                             Column(modifier = Modifier.fillMaxWidth()) {
                                 Row(
                                     verticalAlignment = Alignment.CenterVertically,
                                     modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp) 
                                 ) {
                                     Text(
                                         text = type,
                                         style = MaterialTheme.typography.labelLarge,
                                         fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                                         modifier = Modifier.width(95.dp),
                                         color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
                                     )
                                     
                                     if (slot?.recipe != null) {
                                         val recipe = slot.recipe
                                         Row(
                                             verticalAlignment = Alignment.CenterVertically,
                                             horizontalArrangement = Arrangement.spacedBy(16.dp),
                                             modifier = Modifier
                                                 .weight(1f)
                                                 .clip(RoundedCornerShape(8.dp))
                                                 .clickable { recipe.id?.let { onRecipeClick(it) } }
                                                 .padding(4.dp)
                                         ) {
                                             Box {
                                                 AsyncImage(
                                                     model = recipe.imageUrl,
                                                     contentDescription = null,
                                                     modifier = Modifier
                                                         .size(48.dp)
                                                         .clip(RoundedCornerShape(10.dp))
                                                         .alpha(if (isHandled) 0.5f else 1.0f),
                                                     contentScale = ContentScale.Crop
                                                 )
                                                 if (isHandled) {
                                                     Icon(
                                                         painter = painterResource(com.teamconfused.planmyplate.R.drawable.check_icon),
                                                         contentDescription = null,
                                                         tint = MaterialTheme.colorScheme.primary,
                                                         modifier = Modifier.align(Alignment.Center).size(24.dp)
                                                     )
                                                 }
                                             }
                                             Text(
                                                 text = recipe.name,
                                                 style = MaterialTheme.typography.bodyLarge,
                                                 fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                                                 color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                                 maxLines = 2,
                                                 lineHeight = 20.sp,
                                                 overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                                 textDecoration = if (isHandled) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                             )
                                         }
                                     } else {
                                         Surface(
                                             color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                             shape = RoundedCornerShape(8.dp),
                                             modifier = Modifier.fillMaxWidth()
                                         ) {
                                             Text(
                                                 text = "Not planned", 
                                                 style = MaterialTheme.typography.bodyMedium,
                                                 fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                                 modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                 color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                             )
                                         }
                                     }
                                 }
                                 
                                 additionalForSlot.forEach { additional ->
                                     Row(
                                         verticalAlignment = Alignment.CenterVertically,
                                         modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                     ) {
                                         Text(
                                             text = "Additional",
                                             style = MaterialTheme.typography.labelLarge,
                                             fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                                             modifier = Modifier.width(95.dp),
                                             color = MaterialTheme.colorScheme.secondary
                                         )
                                         
                                         Row(
                                             verticalAlignment = Alignment.CenterVertically,
                                             horizontalArrangement = Arrangement.spacedBy(16.dp),
                                             modifier = Modifier
                                                 .weight(1f)
                                                 .clip(RoundedCornerShape(8.dp))
                                                 .clickable { onRecipeClick(additional.recipeId) }
                                                 .padding(4.dp)
                                         ) {
                                             AsyncImage(
                                                 model = additional.recipe.imageUrl,
                                                 contentDescription = null,
                                                 modifier = Modifier
                                                     .size(48.dp)
                                                     .clip(RoundedCornerShape(10.dp)),
                                                 contentScale = ContentScale.Crop
                                             )
                                             Text(
                                                 text = additional.recipe.name,
                                                 style = MaterialTheme.typography.bodyLarge,
                                                 fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                                                 color = MaterialTheme.colorScheme.onSurface,
                                                 maxLines = 2,
                                                 lineHeight = 20.sp,
                                                 overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                             )
                                         }
                                     }
                                 }
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
