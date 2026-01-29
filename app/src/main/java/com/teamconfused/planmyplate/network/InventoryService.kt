package com.teamconfused.planmyplate.network

import com.teamconfused.planmyplate.model.Inventory
import com.teamconfused.planmyplate.model.InventoryItem
import com.teamconfused.planmyplate.model.InventoryItemRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface InventoryService {
    @GET("api/inventory/user/{userId}")
    suspend fun getInventoryForUser(@Path("userId") userId: Int): Inventory

    @GET("api/inventory/{id}")
    suspend fun getInventoryById(@Path("id") id: Int): Inventory

    @POST("api/inventory/user/{userId}")
    suspend fun createInventoryForUser(@Path("userId") userId: Int): Inventory

    @PUT("api/inventory/{id}")
    suspend fun updateInventory(
        @Path("id") id: Int,
        @Body request: Map<String, Any> = emptyMap()
    ): Inventory

    @DELETE("api/inventory/{id}")
    suspend fun deleteInventory(@Path("id") id: Int): Map<String, String>

    @GET("api/inventory/{inventoryId}/items")
    suspend fun getInventoryItems(@Path("inventoryId") inventoryId: Int): List<InventoryItem>

    @POST("api/inventory/{inventoryId}/items")
    suspend fun addItemToInventory(
        @Path("inventoryId") inventoryId: Int,
        @Body request: InventoryItemRequest
    ): InventoryItem

    @DELETE("api/inventory/items/{itemId}")
    suspend fun removeItemFromInventory(@Path("itemId") itemId: Int): Map<String, String>

    // New Endpoint Requirement: Update Item Quantity
    // This will return 404 until backend implements it.
    @PUT("api/inventory/items/{itemId}")
    suspend fun updateInventoryItem(
        @Path("itemId") itemId: Int, 
        @Body request: InventoryItemRequest
    ): InventoryItem
}
