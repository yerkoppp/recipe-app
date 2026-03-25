package com.ycosoriodev.recipeapp.data.local

import androidx.room.TypeConverter
import com.ycosoriodev.recipeapp.domain.model.Ingredient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class IngredientDto(val name: String, val amount: Double, val unit: String)

fun Ingredient.toDto() = IngredientDto(name, amount, unit)
fun IngredientDto.toDomain() = Ingredient(name, amount, unit)

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromIngredientList(ingredients: List<Ingredient>): String {
        val dtos = ingredients.map { it.toDto() }
        return json.encodeToString(dtos)
    }

    @TypeConverter
    fun toIngredientList(jsonString: String): List<Ingredient> {
        return try {
            json.decodeFromString<List<IngredientDto>>(jsonString).map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun toStringList(jsonString: String): List<String> {
        return try {
            json.decodeFromString<List<String>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
