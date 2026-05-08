package com.teamconfused.planmyplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InventoryDto(
    @SerialName("invId") val id: Int? = null,
    val user: UserRefDto? = null,
    @SerialName("lastUpdate") val lastUpdate: String? = null,
    val items: List<InventoryItemDto>? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class InventoryItemDto(
    @SerialName("itemId") val id: Int? = null,
    val inventoryId: Int? = null,
    val quantity: Int,
    val unit: String? = null,
    @SerialName("dateAdded") val dateAdded: String? = null,
    @SerialName("expiryDate") val expiryDate: String,
    val ingredient: IngredientRefDto? = null,
    val name: String? = null
)

@Serializable
data class InventoryItemRequest(
    val quantity: Int,
    val unit: String? = null,
    @SerialName("dateAdded") val dateAdded: String? = null,
    @SerialName("expiryDate") val expiryDate: String,
    val ingredient: IngredientRefDto
)
