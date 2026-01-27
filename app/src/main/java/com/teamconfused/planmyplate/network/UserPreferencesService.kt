package com.teamconfused.planmyplate.network

import com.teamconfused.planmyplate.model.Allergy
import com.teamconfused.planmyplate.model.Diet
import com.teamconfused.planmyplate.model.Ingredient
import com.teamconfused.planmyplate.model.UserPreferences
import com.teamconfused.planmyplate.model.UserPreferencesRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserPreferencesService {
    @POST("api/user-preferences/{userId}")
    suspend fun setPreferences(
        @Path("userId") userId: Int,
        @Body dto: UserPreferencesRequest
    ): UserPreferences

    @GET("api/user-preferences/{userId}")
    suspend fun getPreferences(@Path("userId") userId: Int): UserPreferences

    @GET("api/reference-data/diets")
    suspend fun getDiets(): List<Diet>

    @GET("api/reference-data/allergies")
    suspend fun getAllergies(): List<Allergy>

    @GET("api/reference-data/dislikes")
    suspend fun getDislikes(): List<Ingredient>
}
