package com.teamconfused.planmyplate.network

import com.teamconfused.planmyplate.model.CreateMealPlanRequest
import com.teamconfused.planmyplate.model.MealPlan
import com.teamconfused.planmyplate.model.MealPlanRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MealPlanService {
    @GET("api/meal-plans/user/{userId}")
    suspend fun getAllMealPlansForUser(@Path("userId") userId: Int): List<MealPlan>

    @GET("api/meal-plans/{id}")
    suspend fun getMealPlanById(@Path("id") id: Int): MealPlan

    @POST("api/meal-plans/user/{userId}")
    suspend fun createMealPlan(
        @Path("userId") userId: Int,
        @Body request: MealPlanRequest
    ): MealPlan

    @POST("api/meal-plans/user/{userId}/create")
    suspend fun createMealPlanWithRecipes(
        @Path("userId") userId: Int,
        @Body request: CreateMealPlanRequest
    ): MealPlan

    @PUT("api/meal-plans/{id}")
    suspend fun updateMealPlan(
        @Path("id") id: Int,
        @Body request: MealPlanRequest
    ): MealPlan

    @DELETE("api/meal-plans/{id}")
    suspend fun deleteMealPlan(@Path("id") id: Int): Map<String, String>

    @GET("api/meal-plans/user/{userId}/status/{status}")
    suspend fun getMealPlansByStatus(
        @Path("userId") userId: Int,
        @Path("status") status: String
    ): List<MealPlan>

    @GET("api/meal-plans/user/{userId}/weekly")
    suspend fun getWeeklyMealPlans(@Path("userId") userId: Int): List<MealPlan>
}
