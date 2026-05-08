package com.teamconfused.planmyplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroceryListDto(
    @SerialName("listId") val listId: Int? = null,
    @SerialName("userId") val userId: Int? = null,
    @SerialName("dateCreated") val dateCreated: String? = null,
    val status: String = "active",
    val items: List<GroceryListItemDto>? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class GroceryListRequest(
    val status: String = "active",
    @SerialName("startDate") val startDate: String? = null
)

@Serializable
data class PurchaseItemDetail(
    val itemId: Int,
    val quantity: Int
)

@Serializable
data class PurchaseItemsRequest(
    val items: List<PurchaseItemDetail>
)

@Serializable
data class GroceryListItemDto(
    val id: Int? = null,
    val ingredient: IngredientDto? = null,
    val quantity: Int? = null,
    val unit: String? = null
)
