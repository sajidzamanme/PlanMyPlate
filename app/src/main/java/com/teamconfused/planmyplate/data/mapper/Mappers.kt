package com.teamconfused.planmyplate.data.mapper

import com.teamconfused.planmyplate.data.model.UserPreferencesDto
import com.teamconfused.planmyplate.data.model.GroceryListDto
import com.teamconfused.planmyplate.data.model.GroceryListItemDto
import com.teamconfused.planmyplate.data.model.IngredientDto
import com.teamconfused.planmyplate.data.model.IngredientRefDto
import com.teamconfused.planmyplate.data.model.InventoryDto
import com.teamconfused.planmyplate.data.model.InventoryItemDto
import com.teamconfused.planmyplate.data.model.MealPlanDto
import com.teamconfused.planmyplate.data.model.MealSlotDto
import com.teamconfused.planmyplate.data.model.RecipeIngredientResponse
import com.teamconfused.planmyplate.data.model.RecipeResponse
import com.teamconfused.planmyplate.data.model.AllergyDto
import com.teamconfused.planmyplate.data.model.DietDto
import com.teamconfused.planmyplate.data.model.UserDto
import com.teamconfused.planmyplate.data.model.UserPreferencesResponse
import com.teamconfused.planmyplate.data.model.ExpiryItemResponse
import com.teamconfused.planmyplate.data.model.SoonToExpireResponse
import com.teamconfused.planmyplate.domain.model.GroceryList
import com.teamconfused.planmyplate.domain.model.GroceryListItem
import com.teamconfused.planmyplate.domain.model.Ingredient
import com.teamconfused.planmyplate.domain.model.Inventory
import com.teamconfused.planmyplate.domain.model.InventoryItem
import com.teamconfused.planmyplate.domain.model.MealPlan
import com.teamconfused.planmyplate.domain.model.MealSlot
import com.teamconfused.planmyplate.domain.model.Recipe
import com.teamconfused.planmyplate.domain.model.Allergy
import com.teamconfused.planmyplate.domain.model.Diet
import com.teamconfused.planmyplate.domain.model.User
import com.teamconfused.planmyplate.domain.model.UserPreferences
import com.teamconfused.planmyplate.domain.model.ExpiryItem
import com.teamconfused.planmyplate.domain.model.SoonToExpireResult

// Recipe Mappers
fun RecipeResponse.toDomain(): Recipe {
    return Recipe(
        id = this.id,
        name = this.name,
        description = this.description ?: "",
        calories = this.calories ?: 0,
        prepTime = this.prepTime,
        cookTime = this.cookTime,
        servings = this.servings,
        instructions = this.instructions,
        ingredients = this.ingredients?.map { it.toIngredientString() },
        imageUrl = this.imageUrl
    )
}

fun RecipeIngredientResponse.toIngredientString(): String {
    val qty = if (this.quantity != null && this.quantity > 0) "${this.quantity} " else ""
    val unitStr = if (!this.unit.isNullOrBlank()) "${this.unit} " else ""
    val name = this.ingredient?.name ?: "Unknown Ingredient"
    return "$qty$unitStr$name".trim()
}

// User Mappers
fun UserDto.toDomain(): User {
    return User(
        userId = this.userId,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        phone = this.phone,
        dateOfBirth = this.dateOfBirth,
        age = this.age,
        weight = this.weight,
        budget = this.budget
    )
}

fun UserPreferencesDto.toDomain(userId: Int? = null): UserPreferences {
    return UserPreferences(
        userId = userId,
        diet = this.diet,
        allergies = this.allergies?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() },
        dislikes = this.dislikes?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() },
        servings = this.servings,
        budget = this.budget,
        age = this.age,
        weight = this.weight
    )
}

fun UserPreferencesResponse.toDomain(): UserPreferences {
    return UserPreferences(
        prefId = this.prefId,
        userId = this.userId,
        diet = this.diet,
        allergies = this.allergies,
        dislikes = this.dislikes,
        servings = this.servings,
        budget = this.budget,
        age = this.age,
        weight = this.weight
    )
}

fun DietDto.toDomain(): Diet {
    return Diet(
        dietId = this.dietId,
        dietName = this.dietName
    )
}

fun AllergyDto.toDomain(): Allergy {
    return Allergy(
        allergyId = this.allergyId,
        allergyName = this.allergyName
    )
}

// Ingredient Mappers
fun IngredientDto.toDomain(): Ingredient {
    return Ingredient(
        ingId = this.ingId,
        name = this.name,
        price = this.price?.toFloatOrNull()
    )
}

fun IngredientRefDto.toDomain(): Ingredient {
    return Ingredient(
        ingId = this.ingId,
        name = this.name ?: "Unknown Ingredient"
    )
}

// Meal Plan Mappers
fun MealPlanDto.toDomain(): MealPlan {
    return MealPlan(
        mpId = this.mpId,
        userId = this.userId,
        startDate = this.startDate,
        duration = this.duration,
        status = this.status,
        slots = this.slots?.map { it.toDomain() }
    )
}

fun MealSlotDto.toDomain(): MealSlot {
    return MealSlot(
        slotId = this.id,
        mealType = this.mealType,
        date = this.date,
        dayNumber = this.dayNumber,
        recipe = this.recipe?.toDomain()
    )
}

// Grocery List Mappers
fun GroceryListDto.toDomain(): GroceryList {
    return GroceryList(
        listId = this.listId,
        userId = this.userId,
        dateCreated = this.dateCreated,
        status = this.status,
        items = this.items?.map { it.toDomain() }
    )
}

fun GroceryListItemDto.toDomain(): GroceryListItem {
    return GroceryListItem(
        id = this.id,
        ingredient = this.ingredient?.toDomain(),
        quantity = this.quantity,
        unit = this.unit
    )
}

// Inventory Mappers
fun InventoryDto.toDomain(): Inventory {
    return Inventory(
        id = this.id,
        userId = this.user?.userId,
        lastUpdate = this.lastUpdate,
        items = this.items?.map { it.toDomain() }
    )
}

fun InventoryItemDto.toDomain(): InventoryItem {
    return InventoryItem(
        id = this.id,
        inventoryId = this.inventoryId,
        quantity = this.quantity,
        unit = this.unit,
        dateAdded = this.dateAdded,
        expiryDate = this.expiryDate,
        ingredient = this.ingredient?.toDomain(),
        name = this.name
    )
}

// Expiry Mappers
fun ExpiryItemResponse.toDomain(): ExpiryItem {
    return ExpiryItem(
        itemId = this.itemId,
        productName = this.productName,
        expiryDate = this.expiryDate,
        dateAdded = this.dateAdded,
        quantity = this.quantity,
        unit = this.unit,
        daysUntilExpiry = this.daysUntilExpiry,
        isExpired = this.isExpired
    )
}

fun SoonToExpireResponse.toDomain(): SoonToExpireResult {
    return SoonToExpireResult(
        thresholdDays = this.thresholdDays,
        totalCount = this.totalCount,
        expiredCount = this.expiredCount,
        items = this.items?.map { it.toDomain() }
    )
}
