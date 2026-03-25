package com.ycosoriodev.recipeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [RecipeEntity::class],
    version = 4
)
@TypeConverters(Converters::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract val recipeDao: RecipeDao
    
    companion object {
        const val DATABASE_NAME = "recipes_db"
    }
}
