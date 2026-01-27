package com.teamconfused.planmyplate.network

import com.teamconfused.planmyplate.model.UpdateUserRequest
import com.teamconfused.planmyplate.model.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    @GET("api/users/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): User

    @GET("api/users/{userId}")
    suspend fun getUserById(@Path("userId") userId: Int): User

    @PUT("api/users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: Int,
        @Body request: UpdateUserRequest
    ): User

    @DELETE("api/users/{userId}")
    suspend fun deleteUser(@Path("userId") userId: Int): Map<String, String>
}
