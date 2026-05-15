package com.teamconfused.planmyplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InventoryDto(
    @SerialName("inv_id") val id: Int? = null,
    val user: UserRefDto? = null,
    @SerialName("last_update") val lastUpdate: String? = null,
    val items: List<InventoryItemDto>? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class InventoryItemDto(
    @SerialName("item_id") val id: Int? = null,
    @SerialName("inventory_id") val inventoryId: Int? = null,
    val quantity: Double? = null,
    val unit: String? = null,
    @SerialName("date_added") val dateAdded: String? = null,
    @SerialName("expiry_date") val expiryDate: String? = null,
    val ingredient: IngredientRefDto? = null,
    val name: String? = null
)

@Serializable
data class InventoryItemRequest(
    val quantity: Double? = null,
    val unit: String? = null,
    @SerialName("date_added") val dateAdded: String? = null,
    @SerialName("expiry_date") val expiryDate: String? = null,
    val ingredient: IngredientRefDto? = null
)