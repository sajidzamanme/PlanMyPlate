package com.teamconfused.planmyplate.domain.repository

import com.teamconfused.planmyplate.model.CreateRecipeRequest
import com.teamconfused.planmyplate.model.Recipe
import com.teamconfused.planmyplate.model.RecipeRequest
import okhttp3.MultipartBody

interface RecipeRepository {
    suspend fun getAllRecipes(): List<Recipe>
    suspend fun getRecipeById(id: Int): Recipe
    suspend fun createRecipe(request: CreateRecipeRequest): Recipe
    suspend fun updateRecipe(id: Int, request: RecipeRequest): Recipe
    suspend fun deleteRecipe(id: Int)
    suspend fun searchRecipes(query: String): List<Recipe>
    suspend fun getRecipesByCalories(min: Int, max: Int): List<Recipe>
    suspend fun uploadImage(file: MultipartBody.Part): String
}
