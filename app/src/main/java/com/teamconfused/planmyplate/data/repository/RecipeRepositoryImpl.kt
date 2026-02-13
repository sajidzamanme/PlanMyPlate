package com.teamconfused.planmyplate.data.repository

import com.teamconfused.planmyplate.domain.repository.RecipeRepository
import com.teamconfused.planmyplate.model.CreateRecipeRequest
import com.teamconfused.planmyplate.model.Recipe
import com.teamconfused.planmyplate.model.RecipeRequest
import com.teamconfused.planmyplate.model.toRecipe
import com.teamconfused.planmyplate.network.RecipeService
import okhttp3.MultipartBody

class RecipeRepositoryImpl(
    private val api: RecipeService
) : RecipeRepository {
    override suspend fun getAllRecipes(): List<Recipe> {
        return api.getAllRecipes().map { it.toRecipe() }
    }

    override suspend fun getRecipeById(id: Int): Recipe {
        return api.getRecipeById(id).toRecipe()
    }

    override suspend fun createRecipe(request: CreateRecipeRequest): Recipe {
        return api.createRecipe(request).toRecipe()
    }

    override suspend fun updateRecipe(id: Int, request: RecipeRequest): Recipe {
        return api.updateRecipe(id, request).toRecipe()
    }

    override suspend fun deleteRecipe(id: Int) {
        api.deleteRecipe(id)
    }

    override suspend fun searchRecipes(query: String): List<Recipe> {
        return api.searchRecipesByName(query).map { it.toRecipe() }
    }

    override suspend fun getRecipesByCalories(min: Int, max: Int): List<Recipe> {
        return api.getRecipesByCalories(min, max).map { it.toRecipe() }
    }

    override suspend fun uploadImage(file: MultipartBody.Part): String {
        return api.uploadImage(file).url
    }
}
