package com.teamconfused.planmyplate.network

import com.teamconfused.planmyplate.model.CreateRecipeRequest
import com.teamconfused.planmyplate.model.ImageUploadResponse
import com.teamconfused.planmyplate.model.RecipeIngredientRequest
import com.teamconfused.planmyplate.model.RecipeRequest
import com.teamconfused.planmyplate.model.RecipeResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeService {
    @GET("api/recipes")
    suspend fun getAllRecipes(): List<RecipeResponse>

    @GET("api/recipes/{id}")
    suspend fun getRecipeById(@Path("id") id: Int): RecipeResponse

    @Multipart
    @POST("api/files/upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part): ImageUploadResponse

    @POST("api/recipes")
    suspend fun createRecipe(@Body request: CreateRecipeRequest): RecipeResponse

    @PUT("api/recipes/{id}")
    suspend fun updateRecipe(
        @Path("id") id: Int,
        @Body request: RecipeRequest
    ): RecipeResponse

    @DELETE("api/recipes/{id}")
    suspend fun deleteRecipe(@Path("id") id: Int): Map<String, String>

    @GET("api/recipes/search")
    suspend fun searchRecipesByName(@Query("name") name: String): List<RecipeResponse>

    @GET("api/recipes/filter/calories")
    suspend fun getRecipesByCalories(
        @Query("minCalories") minCalories: Int,
        @Query("maxCalories") maxCalories: Int
    ): List<RecipeResponse>
}
