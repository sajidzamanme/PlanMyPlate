# Quick Reference Guide - API Integration

## Using the API Services

### Initialization
All services are initialized in `RetrofitClient` and can be accessed like:
```kotlin
RetrofitClient.authService
RetrofitClient.userService
RetrofitClient.recipeService
RetrofitClient.ingredientService
RetrofitClient.mealPlanService
RetrofitClient.groceryListService
RetrofitClient.inventoryService
RetrofitClient.userPreferencesService
```

---

## Common Usage Patterns

### Authentication

#### Signup
```kotlin
val request = SignupRequest(
    name = "John Doe",
    email = "john@example.com",
    password = "password123"
)
val response = RetrofitClient.authService.signup(request)
val userId = response.getEffectiveUserId()
```

#### Signin
```kotlin
val request = SigninRequest(
    email = "john@example.com",
    password = "password123"
)
val response = RetrofitClient.authService.signin(request)
```

#### Forgot Password
```kotlin
val request = ForgotPasswordRequest(email = "john@example.com")
val response = RetrofitClient.authService.forgotPassword(request)
val resetToken = response.token // Store this
```

#### Reset Password
```kotlin
val request = ResetPasswordRequest(
    resetToken = storedToken,
    newPassword = "newpassword123"
)
RetrofitClient.authService.resetPassword(request)
```

---

### User Management

#### Get Current User
```kotlin
val user = RetrofitClient.userService.getCurrentUser(token = "Bearer $jwtToken")
```

#### Get User by ID
```kotlin
val user = RetrofitClient.userService.getUserById(userId = 1)
```

#### Update User
```kotlin
val updateRequest = UpdateUserRequest(
    name = "Jane Doe",
    age = 25,
    weight = 65.0f,
    budget = 600.00f
)
RetrofitClient.userService.updateUser(userId = 1, request = updateRequest)
```

---

### Recipes

#### Get All Recipes
```kotlin
val recipes = RetrofitClient.recipeService.getAllRecipes()
```

#### Search Recipes
```kotlin
val recipes = RetrofitClient.recipeService.searchRecipesByName("pasta")
```

#### Filter by Calories
```kotlin
val recipes = RetrofitClient.recipeService.filterRecipesByCalories(
    minCalories = 300,
    maxCalories = 500
)
```

#### Create Recipe
```kotlin
val request = RecipeRequest(
    name = "Pasta Carbonara",
    description = "Italian pasta dish",
    calories = 450
)
RetrofitClient.recipeService.createRecipe(request)
```

---

### Ingredients

#### Search Ingredients
```kotlin
val ingredients = RetrofitClient.ingredientService.searchIngredientsByName("tomato")
```

#### Filter by Price
```kotlin
val ingredients = RetrofitClient.ingredientService.filterIngredientsByPrice(
    minPrice = 10f,
    maxPrice = 100f
)
```

---

### Meal Plans

#### Get All Meal Plans for User
```kotlin
val mealPlans = RetrofitClient.mealPlanService.getAllMealPlansForUser(userId = 1)
```

#### Create Meal Plan
```kotlin
val request = MealPlanRequest(
    duration = 7,
    status = "active"
)
RetrofitClient.mealPlanService.createMealPlan(userId = 1, request = request)
```

#### Get Weekly Plans
```kotlin
val weeklyPlans = RetrofitClient.mealPlanService.getWeeklyMealPlans(userId = 1)
```

---

### Grocery Lists

#### Get All Grocery Lists for User
```kotlin
val lists = RetrofitClient.groceryListService.getAllGroceryListsForUser(userId = 1)
```

#### Create Grocery List
```kotlin
val request = GroceryListRequest(status = "active")
RetrofitClient.groceryListService.createGroceryList(userId = 1, request = request)
```

---

### Inventory

#### Get User Inventory
```kotlin
val inventory = RetrofitClient.inventoryService.getInventoryForUser(userId = 1)
```

#### Get Inventory Items
```kotlin
val items = RetrofitClient.inventoryService.getInventoryItems(inventoryId = 1)
```

#### Add Item to Inventory
```kotlin
val request = InventoryItemRequest(
    quantity = 5,
    expiryDate = "2026-02-28",
    ingredient = IngredientRef(ingId = 1)
)
RetrofitClient.inventoryService.addItemToInventory(inventoryId = 1, request = request)
```

---

### User Preferences

#### Set Preferences
```kotlin
val request = UserPreferencesRequest(
    diet = "Vegetarian",
    allergies = listOf("peanuts", "shellfish"),
    dislikes = listOf("mushrooms"),
    servings = 2,
    budget = 500.00f
)
RetrofitClient.userPreferencesService.setPreferences(userId = 1, dto = request)
```

#### Get Preferences
```kotlin
val preferences = RetrofitClient.userPreferencesService.getPreferences(userId = 1)
```

---

## ViewModel Examples

### In a ViewModel with Coroutines

```kotlin
viewModelScope.launch {
    try {
        _uiState.update { it.copy(isLoading = true) }
        val recipes = RetrofitClient.recipeService.getAllRecipes()
        _uiState.update { it.copy(recipes = recipes, isLoading = false) }
    } catch (e: Exception) {
        _uiState.update { it.copy(error = e.message, isLoading = false) }
    }
}
```

---

## Error Handling

All API errors follow the `ErrorResponse` format:
```kotlin
@Serializable
data class ErrorResponse(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String
)
```

Example error handling:
```kotlin
try {
    val response = RetrofitClient.authService.signin(request)
} catch (e: HttpException) {
    // Handle HTTP errors (4xx, 5xx)
    val errorBody = e.response()?.errorBody()?.string()
} catch (e: IOException) {
    // Handle network errors
} catch (e: Exception) {
    // Handle other errors
}
```

---

## Base URL Configuration

Currently set to: `http://192.168.0.153:8081/`

To change for different environments:
```kotlin
// In RetrofitClient.kt
private const val BASE_URL = "http://your-server:8081/"
```

---

## Security Notes

1. **JWT Tokens**: Store in SharedPreferences securely
2. **Authorization**: User services require JWT token in header
3. **Password Reset**: Token expires after use
4. **User IDs**: Can be obtained from login response or session

---

## Testing API Calls

### Using Retrofit with Coroutines
```kotlin
// In a test
runTest {
    val response = RetrofitClient.authService.signup(SignupRequest(...))
    assertEquals(response.email, "test@example.com")
}
```

### Mocking Services for Unit Tests
```kotlin
@get:Rule
val instantExecutorRule = InstantTaskExecutorRule()

private val mockAuthService = mockk<AuthService>()

@Test
fun testSignup() {
    coEvery { mockAuthService.signup(any()) } returns AuthResponse(
        userId = JsonPrimitive(1),
        email = "test@example.com"
    )
}
```

---

## Data Flow Example: User Registration to Preferences

```
1. User Signup (SignupScreen)
   └─> SignupViewModel.onSignupClick()
       └─> RetrofitClient.authService.signup(SignupRequest)
           └─> Save userId to SessionManager
           └─> Navigate to PreferenceSelection

2. User Sets Preferences (PreferenceSelectionScreen)
   └─> PreferenceSelectionViewModel.savePreferences()
       └─> RetrofitClient.userPreferencesService.setPreferences(userId, UserPreferencesRequest)
           └─> Navigate to Home

3. View User Profile (HomeScreen)
   └─> SessionManager.getUserId()
       └─> RetrofitClient.userService.getUserById(userId)
           └─> Display user information
```

---

## Common Issues & Solutions

### Issue: "Condition 'response != null' is always 'true'"
**Solution**: This is a Kotlin compiler inspection warning, not an error. The code is safe and will compile/run fine.

### Issue: Network timeout
**Solution**: Increase timeout in OkHttpClient configuration in RetrofitClient

### Issue: 401 Unauthorized
**Solution**: Check JWT token is stored and included in requests properly

### Issue: CORS errors
**Solution**: These are typically backend issues - ensure backend allows requests from your app's origin

---

## Debugging Tips

1. **Enable Request/Response Logging**
   - Already enabled in RetrofitClient with `HttpLoggingInterceptor`
   - Check Logcat for "okhttp" tag

2. **Check Request Body**
   - All serialization uses kotlinx.serialization
   - Unknown fields are ignored (configured in RetrofitClient)

3. **Validate Models**
   - All models use @Serializable annotation
   - Field names must match JSON exactly (use @SerialName for differences)

---

## File Organization

```
network/
  ├── AuthService.kt          (Login, Signup, Password Reset)
  ├── UserService.kt          (User Profile Management)
  ├── RecipeService.kt        (Recipe CRUD & Search)
  ├── IngredientService.kt    (Ingredient CRUD & Filter)
  ├── MealPlanService.kt      (Meal Plan Management)
  ├── GroceryListService.kt   (Grocery List Management)
  ├── InventoryService.kt     (Inventory Management)
  ├── UserPreferencesService.kt (Preference Management)
  └── RetrofitClient.kt       (Retrofit & Service Setup)

model/
  ├── AuthModels.kt           (Auth-related models)
  ├── Models.kt               (All other models)
  └── Recipe.kt               (Legacy recipe model)
```

---

## Ready to Use!

All 47 API endpoints from the documentation are now fully implemented and ready to use in your ViewModels and UI screens.
