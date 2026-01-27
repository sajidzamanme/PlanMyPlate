package com.teamconfused.planmyplate.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ==================== User Related Models ====================

@Serializable
data class User(
    @SerialName("userId") val userId: Int,
    @SerialName("userName") val userName: String? = null,
    val name: String,
    val email: String,
    val password: String? = null,
    val age: Int? = null,
    val weight: Float? = null,
    val budget: Float? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class UpdateUserRequest(
    val name: String? = null,
    @SerialName("userName") val userName: String? = null,
    val age: Int? = null,
    val weight: Float? = null,
    val budget: Float? = null
)

// ==================== Recipe Related Models ====================

@Serializable
data class RecipeResponse(
    @SerialName("recipeId") val id: Int? = null,
    val name: String,
    val description: String? = null,
    val calories: Int? = null
)

@Serializable
data class RecipeRequest(
    val name: String,
    val description: String? = null,
    val calories: Int? = null
)

// ==================== Ingredient Related Models ====================

@Serializable
data class Ingredient(
    @SerialName("ingId") val ingId: Int,
    val name: String,
    val price: Float? = null
)

@Serializable
data class IngredientRequest(
    val name: String,
    val price: Float? = null
)

// ==================== Meal Plan Related Models ====================

@Serializable
data class MealPlan(
    val id: Int? = null,
    @SerialName("userId") val userId: Int? = null,
    @SerialName("startDate") val startDate: String? = null,
    val duration: Int,
    val status: String = "active",
    val meals: List<MealPlanItem>? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class MealPlanItem(
    val day: Int,
    val mealType: String, // Breakfast, Lunch, Dinner
    val recipeId: Int,
    val recipe: RecipeResponse? = null // Optional, for reading
)

@Serializable
data class CreateMealPlanRequest(
    val recipeIds: List<Int>,
    val duration: Int,
    @SerialName("startDate") val startDate: String
)

@Serializable
data class MealPlanRequest(
    val duration: Int,
    val status: String = "active",
    val meals: List<MealPlanItemRequest>
)

@Serializable
data class MealPlanItemRequest(
    val day: Int,
    val mealType: String,
    val recipeId: Int
)

// ==================== Grocery List Related Models ====================

@Serializable
data class GroceryList(
    val id: Int? = null,
    @SerialName("userId") val userId: Int? = null,
    @SerialName("dateCreated") val dateCreated: String? = null,
    val status: String = "active",
    val items: List<GroceryListItem>? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class GroceryListRequest(
    val status: String = "active"
)

@Serializable
data class PurchaseItemsRequest(
    val ingredientIds: List<Int>
)

@Serializable
data class GroceryListItem(
    val id: Int? = null,
    @SerialName("ingredientId") val ingredientId: Int? = null,
    val itemName: String,
    val quantity: Int? = null,
    val unit: String? = null,
    val price: Float? = null
)

// ==================== Inventory Related Models ====================

@Serializable
data class Inventory(
    val id: Int? = null,
    @SerialName("userId") val userId: Int? = null,
    @SerialName("lastUpdate") val lastUpdate: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class InventoryItem(
    val id: Int? = null,
    @SerialName("inventoryId") val inventoryId: Int,
    val quantity: Int,
    @SerialName("expiryDate") val expiryDate: String,
    val ingredient: IngredientRef? = null
)

@Serializable
data class IngredientRef(
    @SerialName("ingId") val ingId: Int
)

@Serializable
data class InventoryItemRequest(
    val quantity: Int,
    @SerialName("expiryDate") val expiryDate: String,
    val ingredient: IngredientRef
)

// ==================== User Preferences Related Models ====================

@Serializable
data class Diet(
    val dietId: Int,
    val dietName: String
)

@Serializable
data class Allergy(
    val allergyId: Int,
    val allergyName: String
)

@Serializable
data class UserPreferences(
    @SerialName("prefId") val prefId: Int? = null,
    @SerialName("userId") val userId: Int? = null,
    val diet: String? = null,
    val allergies: List<String>? = null,
    val dislikes: List<String>? = null,
    val servings: Int? = null,
    val budget: Float? = null,
    val age: Int? = null,
    val weight: Float? = null
)

@Serializable
data class UserPreferencesRequest(
    val diet: String? = null,
    val allergies: List<String>? = null,
    val dislikes: List<String>? = null,
    val servings: Int? = null,
    val budget: Float? = null
)

// ==================== Error Response Model ====================

@Serializable
data class ErrorResponse(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String
)
