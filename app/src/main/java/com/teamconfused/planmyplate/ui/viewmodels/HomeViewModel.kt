package com.teamconfused.planmyplate.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamconfused.planmyplate.model.Recipe
import com.teamconfused.planmyplate.model.toRecipe
import com.teamconfused.planmyplate.network.RecipeService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val todayBreakfast: Recipe? = null,
    val todayLunch: Recipe? = null,
    val todayDinner: Recipe? = null,
    val upcomingMeals: List<Recipe> = emptyList(),
    val upcomingDayLabel: String? = null,
    val upcomingMessage: String? = null,
    val errorMessage: String? = null
) {
    val todayCalories: Int
        get() = (todayBreakfast?.calories ?: 0) + 
                (todayLunch?.calories ?: 0) + 
                (todayDinner?.calories ?: 0)
}

class HomeViewModel(
    private val recipeService: RecipeService,
    private val mealPlanService: com.teamconfused.planmyplate.network.MealPlanService,
    private val sessionManager: com.teamconfused.planmyplate.util.SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchTodaysMeals()
    }

    fun fetchTodaysMeals() {
        val userId = sessionManager.getUserId()
        if (userId == -1) {
             _uiState.update { it.copy(isLoading = false, errorMessage = "User not logged in") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Fetch weekly meal plans first
                val plans = mealPlanService.getWeeklyMealPlans(userId)
                
                // Find active plan
                val activePlan = plans.find { it.status == "active" }
                
                if (activePlan != null && !activePlan.slots.isNullOrEmpty()) {
                    // Update session state to ensure UI shows dashboard on next launch too or if inconsistent
                    sessionManager.setHasMealPlans(true)

                    // Logic to find "today's" meals based on dates in slots
                    // Enriched slots with computed date if missing
                    // Use robust inference similar to MealPlanScreen
                    val enrichedSlots = activePlan.slots.mapIndexed { index, slot ->
                        // Determine Day Index (1..7)
                        val dayIndex = if (slot.dayNumber != null && slot.dayNumber > 0) slot.dayNumber
                                       else if (slot.day != null && slot.day > 0) slot.day
                                       else {
                                           // Try date derivation
                                           val derived = if (slot.date != null && activePlan.startDate != null) {
                                               try {
                                                   java.time.temporal.ChronoUnit.DAYS.between(
                                                       java.time.LocalDate.parse(activePlan.startDate),
                                                       java.time.LocalDate.parse(slot.date)
                                                   ).toInt() + 1
                                               } catch (e: Exception) { 0 }
                                           } else 0
                                           
                                           if (derived > 0) derived else (index / 3) + 1
                                       }

                        val computedDate = if (activePlan.startDate != null) {
                             try {
                                 java.time.LocalDate.parse(activePlan.startDate)
                                     .plusDays((dayIndex - 1).toLong())
                                     .toString()
                             } catch (e: Exception) { "Day $dayIndex" }
                        } else {
                             // Fallback if no start date, just group by day index string if needed or keep loose
                             // If we have slot.date, use it, else Day X
                             slot.date ?: "Day $dayIndex"
                        }
                        slot to computedDate
                    }

                    // Group slots by date and sort
                    val slotsByDate = enrichedSlots.groupBy { it.second }.toSortedMap()
                    val dates = slotsByDate.keys.toList()

                    // Logic to find Today and Next Day
                    val todayDateString = java.time.LocalDate.now().toString()
                    
                    // Identify key for "Today"
                    // If today exists in plan, use it. Else if plan starts in future, use first day as "Today" (preview).
                    // If plan ended, show nothing or last day? Assuming active plan means relevant.
                    
                    val todayKey = if (slotsByDate.containsKey(todayDateString)) {
                        todayDateString
                    } else {
                        // If today is not in keys, pick the first available date (assuming upcoming plan)
                        dates.firstOrNull { it >= todayDateString } ?: dates.firstOrNull()
                    }
                    
                    // Identify Next Day (day after todayKey)
                    val todayIndex = dates.indexOf(todayKey)
                    val nextDayKey = if (todayIndex != -1 && todayIndex + 1 < dates.size) {
                        dates[todayIndex + 1]
                    } else {
                        null
                    }

                    val todayMealsList = if (todayKey != null) slotsByDate[todayKey]?.map { it.first } ?: emptyList() else emptyList()
                    val upcomingMealsList = if (nextDayKey != null) slotsByDate[nextDayKey]?.map { it.first } ?: emptyList() else emptyList()

                    // Extract Today's specific meals
                    val todayBreakfast = todayMealsList.find { it.mealType == "Breakfast" }?.let { getRecipe(it) }
                    val todayLunch = todayMealsList.find { it.mealType == "Lunch" }?.let { getRecipe(it) }
                    val todayDinner = todayMealsList.find { it.mealType == "Dinner" }?.let { getRecipe(it) }
                    
                    // Extract Upcoming Meals (All of them)
                    val upcomingRecipes = upcomingMealsList.mapNotNull { getRecipe(it) }
                    
                    val upcomingMsg = if (nextDayKey == null && todayKey != null) "No upcoming meals (End of Plan)" else null
                    val upcomingLabel = if (nextDayKey != null) "Tomorrow" else null // Simplified label logic

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            todayBreakfast = todayBreakfast,
                            todayLunch = todayLunch,
                            todayDinner = todayDinner,
                            upcomingMeals = upcomingRecipes,
                            upcomingDayLabel = upcomingLabel,
                            upcomingMessage = upcomingMsg
                        )
                    }
                } else {
                     _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null // empty dashboard is handled by UI
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to fetch meals"
                    )
                }
            }
        }
    }

    fun retry() {
        fetchTodaysMeals()
    }

    private fun getRecipe(item: com.teamconfused.planmyplate.model.MealSlot): Recipe? {
        return item.recipe?.toRecipe()
    }
}
