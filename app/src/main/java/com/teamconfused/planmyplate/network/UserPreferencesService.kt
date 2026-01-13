package com.teamconfused.planmyplate.network

import com.teamconfused.planmyplate.model.UserPreferencesDto
import retrofit2.http.*

interface UserPreferencesService {
    @POST("api/user-preferences/{userId}")
    suspend fun setPreferences(
        @Path("userId") userId: Int,
        @Body dto: UserPreferencesDto
    ): UserPreferencesDto

    @GET("api/user-preferences/{userId}")
    suspend fun getPreferences(@Path("userId") userId: Int): UserPreferencesDto
}
