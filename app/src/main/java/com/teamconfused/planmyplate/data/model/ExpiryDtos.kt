package com.teamconfused.planmyplate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExpiryItemRequest(
    @SerialName("product_name") val productName: String,
    @SerialName("expiry_date") val expiryDate: String,
    val quantity: Double? = null,
    val unit: String? = null
)

@Serializable
data class ExpiryItemResponse(
    @SerialName("item_id") val itemId: Int? = null,
    @SerialName("product_name") val productName: String? = null,
    @SerialName("expiry_date") val expiryDate: String? = null,
    @SerialName("date_added") val dateAdded: String? = null,
    val quantity: Double? = null,
    val unit: String? = null,
    @SerialName("days_until_expiry") val daysUntilExpiry: Int? = null,
    @SerialName("is_expired") val isExpired: Boolean? = null
)

@Serializable
data class SoonToExpireResponse(
    @SerialName("threshold_days") val thresholdDays: Int? = null,
    @SerialName("total_count") val totalCount: Int? = null,
    @SerialName("expired_count") val expiredCount: Int? = null,
    val items: List<ExpiryItemResponse>? = null
)

@Serializable
data class UpdateExpiryRequest(
    @SerialName("expiry_date") val expiryDate: String? = null,
    val quantity: Double? = null,
    val unit: String? = null
)
