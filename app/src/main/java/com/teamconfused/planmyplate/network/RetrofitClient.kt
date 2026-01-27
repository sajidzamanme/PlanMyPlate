package com.teamconfused.planmyplate.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object RetrofitClient {
    private const val BASE_URL = "http://192.168.0.153:8081/" // 10.0.2.2 is localhost for Android Emulator

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    // Authentication Service
    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    // User Services
    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    val userPreferencesService: UserPreferencesService by lazy {
        retrofit.create(UserPreferencesService::class.java)
    }

    // Recipe Service
    val recipeService: RecipeService by lazy {
        retrofit.create(RecipeService::class.java)
    }

    // Ingredient Service
    val ingredientService: IngredientService by lazy {
        retrofit.create(IngredientService::class.java)
    }

    // Meal Plan Service
    val mealPlanService: MealPlanService by lazy {
        retrofit.create(MealPlanService::class.java)
    }

    // Grocery List Service
    val groceryListService: GroceryListService by lazy {
        retrofit.create(GroceryListService::class.java)
    }

    // Inventory Service
    val inventoryService: InventoryService by lazy {
        retrofit.create(InventoryService::class.java)
    }
}
