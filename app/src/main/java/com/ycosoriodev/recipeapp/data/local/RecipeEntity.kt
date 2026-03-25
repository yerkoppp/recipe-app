package com.ycosoriodev.recipeapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ycosoriodev.recipeapp.domain.model.Recipe
import com.ycosoriodev.recipeapp.domain.model.Ingredient

import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val portions: Int,
    val ingredients: List<Ingredient>, // TypeConverter needed
    val steps: List<String>,       // TypeConverter needed
    val photos: List<String>,      // TypeConverter needed
    val creationDate: Long,
    val categories: List<String>,
    val isFavorite: Boolean
)

fun RecipeEntity.toDomain(): Recipe {
    return Recipe(
        id = id,
        title = title,
        description = description,
        portions = portions,
        ingredients = ingredients,
        steps = steps,
        photos = photos,
        creationDate = creationDate,
        categories = categories,
        isFavorite = isFavorite
    )
}

fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = id,
        title = title,
        description = description,
        portions = portions,
        ingredients = ingredients,
        steps = steps,
        photos = photos,
        creationDate = creationDate,
        categories = categories,
        isFavorite = isFavorite
    )
}
