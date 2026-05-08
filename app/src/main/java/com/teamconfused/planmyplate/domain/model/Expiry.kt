package com.teamconfused.planmyplate.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ExpiryItem(
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
data class SoonToExpireResult(
    val thresholdDays: Int,
    val totalCount: Int,
    val expiredCount: Int,
    val items: List<ExpiryItem>
)
