package com.teamconfused.planmyplate.data.repository

import com.teamconfused.planmyplate.data.mapper.toDomain
import com.teamconfused.planmyplate.data.model.CreateRecipeRequest
import com.teamconfused.planmyplate.data.model.RecipeRequest
import com.teamconfused.planmyplate.domain.model.Recipe
import com.teamconfused.planmyplate.domain.repository.RecipeRepository
import com.teamconfused.planmyplate.network.RecipeService
import okhttp3.MultipartBody

class RecipeRepositoryImpl(
    private val api: RecipeService
) : RecipeRepository {
    override suspend fun getAllRecipes(token: String): List<Recipe> {
        return api.getAllRecipes(token).map { it.toDomain() }
    }

    override suspend fun getRecipeById(token: String, id: Int): Recipe {
        return api.getRecipeById(token, id).toDomain()
    }

    override suspend fun createRecipe(token: String, request: CreateRecipeRequest): Recipe {
        return api.createRecipe(token, request).toDomain()
    }

    override suspend fun updateRecipe(token: String, id: Int, request: RecipeRequest): Recipe {
        return api.updateRecipe(token, id, request).toDomain()
    }

    override suspend fun deleteRecipe(token: String, id: Int) {
        api.deleteRecipe(token, id)
    }

    override suspend fun searchRecipes(token: String, query: String): List<Recipe> {
        return api.searchRecipesByName(token, query).map { it.toDomain() }
    }

    override suspend fun getRecipesByCalories(token: String, min: Int, max: Int): List<Recipe> {
        return api.getRecipesByCalories(token, min, max).map { it.toDomain() }
    }

    override suspend fun uploadImage(token: String, file: MultipartBody.Part): String {
        return api.uploadImage(token, file).url
    }
}
