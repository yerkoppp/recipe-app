package com.ycosoriodev.recipeapp.domain.model

import java.util.UUID

data class Recipe(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val portions: Int = 1,
    val ingredients: List<Ingredient> = emptyList(),
    val steps: List<String> = emptyList(),
    val photos: List<String> = emptyList(), // URI strings
    val creationDate: Long = System.currentTimeMillis(),
    val categories: List<String> = emptyList(),
    val isFavorite: Boolean = false
)
