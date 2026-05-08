package com.teamconfused.planmyplate.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Inventory(
    val id: Int? = null,
    val userId: Int? = null,
    val lastUpdate: String? = null,
    val items: List<InventoryItem>? = null,
    val createdAt: String? = null
)

@Serializable
data class InventoryItem(
    val id: Int? = null,
    val inventoryId: Int? = null,
    val quantity: Int,
    val unit: String? = null,
    val dateAdded: String? = null,
    val expiryDate: String,
    val ingredient: Ingredient? = null,
    val name: String? = null
)
