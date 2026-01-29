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
    val calories: Int? = null,
    val prepTime: Int? = null,
    val cookTime: Int? = null,
    val servings: Int? = null,
    val instructions: String? = null
)

@Serializable
data class RecipeRequest(
    val name: String,
    val description: String? = null,
    val calories: Int? = null
)

@Serializable
data class CreateRecipeRequest(
    val name: String,
    val description: String? = null,
    val calories: Int? = null,
    val prepTime: Int? = null,
    val cookTime: Int? = null,
    val servings: Int? = null,
    val instructions: String? = null,
    val ingredients: List<RecipeIngredientRequest>? = null
)

@Serializable
data class RecipeIngredientRequest(
    val ingId: Int,
    val quantity: Int,
    val unit: String
)

// ==================== Ingredient Related Models ====================


@Serializable
data class IngredientRequest(
    val name: String,
    val price: Float? = null
)

@Serializable
data class Ingredient(
    @SerialName("ingId") val ingId: Int? = null,
    val name: String,
    val price: Float? = null
)

// ==================== Meal Plan Related Models ====================

@Serializable
data class MealPlan(
    @SerialName("mpId") val mpId: Int? = null,
    @SerialName("userId") val userId: Int? = null,
    @SerialName("startDate") val startDate: String? = null,
    val duration: Int,
    val status: String = "active",
    val slots: List<MealSlot>? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class MealSlot(
    @SerialName("slotId") val slotId: Int? = null,
    val mealType: String, // Breakfast, Lunch, Dinner
    val date: String? = null,
    val day: Int? = null,
    @SerialName("day_number") val dayNumber: Int? = null,
    val recipe: RecipeResponse? = null
)

@Serializable
data class CreateMealPlanRequest(
    val recipeIds: List<Int>,
    val duration: Int,
    @SerialName("startDate") val startDate: String
)

@Serializable
data class MealPlanRequest(
    @SerialName("startDate") val startDate: String,
    val duration: Int
)

// ==================== Grocery List Related Models ====================

@Serializable
data class GroceryList(
    @SerialName("listId") val listId: Int? = null,
    @SerialName("userId") val userId: Int? = null,
    @SerialName("dateCreated") val dateCreated: String? = null,
    val status: String = "active",
    val items: List<GroceryListItem>? = null, // Changed from ingredients to items
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class GroceryListRequest(
    val status: String = "active",
    @SerialName("startDate") val startDate: String? = null
)

@Serializable
data class PurchaseItemsRequest(
    val ingredientIds: List<Int>
)

@Serializable
data class GroceryListItem(
    val id: Int? = null, // This is the grocery_list_ingredients.id (itemId)
    val ingredient: Ingredient? = null, // Nested ingredient details
    val quantity: Int? = null,
    val unit: String? = null
)

// ==================== Inventory Related Models ====================

@Serializable
data class Inventory(
    @SerialName("invId") val id: Int? = null,
    @SerialName("userId") val userId: Int? = null,
    @SerialName("lastUpdate") val lastUpdate: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class InventoryItem(
    @SerialName("itemId") val id: Int? = null,
    // API returns nested "inventory" object, not "inventoryId". Making nullable to avoid parsing error.
    val inventoryId: Int? = null, 
    val quantity: Int,
    @SerialName("expiryDate") val expiryDate: String,
    val ingredient: IngredientRef? = null,
    val name: String? = null // For fallback display
)

@Serializable
data class IngredientRef(
    @SerialName("ingId") val ingId: Int,
    val name: String? = null // Captured if API returns it inside ingredient object
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
