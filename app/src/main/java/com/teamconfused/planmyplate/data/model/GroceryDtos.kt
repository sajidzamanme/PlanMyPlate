package com.teamconfused.planmyplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroceryListDto(
    @SerialName("list_id") val listId: Int? = null,
    @SerialName("user_id") val userId: Int? = null,
    @SerialName("date_created") val dateCreated: String? = null,
    val status: String = "active",
    val items: List<GroceryListItemDto>? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class GroceryListRequest(
    val status: String = "active",
    @SerialName("start_date") val startDate: String? = null
)

@Serializable
data class PurchaseItemDetail(
    @SerialName("item_id") val itemId: Int,
    val quantity: Double
)

@Serializable
data class PurchaseItemsRequest(
    val items: List<PurchaseItemDetail>
)

@Serializable
data class GroceryListItemDto(
    val id: Int? = null,
    val ingredient: IngredientDto? = null,
    val quantity: Double? = null,
    val unit: String? = null
)