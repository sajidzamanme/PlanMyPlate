package com.teamconfused.planmyplate.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ExpiryItemRequest(
    val productName: String,
    val expiryDate: String,
    val quantity: Float = 1.0f,
    val unit: String = "unit"
)

@Serializable
data class ExpiryItemResponse(
    val itemId: Int,
    val productName: String,
    val expiryDate: String,
    val dateAdded: String,
    val quantity: Float,
    val unit: String,
    val daysUntilExpiry: Int,
    val isExpired: Boolean
)

@Serializable
data class SoonToExpireResponse(
    val thresholdDays: Int,
    val totalCount: Int,
    val expiredCount: Int,
    val items: List<ExpiryItemResponse>
)

@Serializable
data class UpdateExpiryRequest(
    val expiryDate: String? = null,
    val quantity: Float? = null,
    val unit: String? = null
)
