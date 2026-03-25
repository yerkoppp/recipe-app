package com.ycosoriodev.recipeapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    val name: String,
    val amount: Double,
    val unit: String
)
