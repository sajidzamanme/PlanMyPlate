package com.teamconfused.planmyplate.model

data class Recipe(
    val title: String,
    val description: String,
    val price: Double,
    val ingredients: List<String>,
    val imageUrl: String
)