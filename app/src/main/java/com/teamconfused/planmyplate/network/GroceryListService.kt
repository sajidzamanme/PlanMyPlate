package com.teamconfused.planmyplate.network

import com.teamconfused.planmyplate.model.GroceryList
import com.teamconfused.planmyplate.model.GroceryListRequest
import com.teamconfused.planmyplate.model.PurchaseItemsRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GroceryListService {
    @GET("api/grocery-lists/user/{userId}")
    suspend fun getGroceryListsForUser(@Path("userId") userId: Int): List<GroceryList>

    @GET("api/grocery-lists/{id}")
    suspend fun getGroceryListById(@Path("id") id: Int): GroceryList

    @POST("api/grocery-lists/user/{userId}")
    suspend fun createGroceryList(
        @Path("userId") userId: Int,
        @Body request: GroceryListRequest
    ): GroceryList

    @PUT("api/grocery-lists/{id}")
    suspend fun updateGroceryList(
        @Path("id") id: Int,
        @Body request: GroceryListRequest
    ): GroceryList

    @POST("api/grocery-lists/{id}/purchase")
    suspend fun purchaseItems(
        @Path("id") id: Int,
        @Body request: PurchaseItemsRequest
    ): retrofit2.Response<Unit>

    @DELETE("api/grocery-lists/{id}")
    suspend fun deleteGroceryList(@Path("id") id: Int): Map<String, String>

    @GET("api/grocery-lists/user/{userId}/status/{status}")
    suspend fun getGroceryListsByStatus(
        @Path("userId") userId: Int,
        @Path("status") status: String
    ): List<GroceryList>

    // New Endpoint for updating individual grocery list items
    @PUT("api/grocery-lists/{listId}/items/{itemId}")
    suspend fun updateGroceryListItem(
        @Path("listId") listId: Int,
        @Path("itemId") itemId: Int,
        @Body request: Map<String, Any>
    ): com.teamconfused.planmyplate.model.GroceryListItem
}
