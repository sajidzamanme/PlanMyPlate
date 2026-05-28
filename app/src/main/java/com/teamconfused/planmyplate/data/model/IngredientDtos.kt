package com.teamconfused.planmyplate.data.model

import kotlinx.serialization.Serializable

@Serializable
data class IngredientDto(
    val ingId: Int? = null,
    val name: String,
    val price: Double? = null,
    val tags: List<String>? = null
)

@Serializable
data class IngredientRequest(
    val name: String,
    val price: Float? = null
)

@Serializable
data class IngredientRefDto(
    val ingId: Int,
    val name: String? = null
)
