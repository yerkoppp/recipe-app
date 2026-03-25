package com.ycosoriodev.recipeapp.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ycosoriodev.recipeapp.domain.model.Ingredient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RecipeDaoTest {
    private lateinit var dao: RecipeDao
    private lateinit var db: RecipeDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, RecipeDatabase::class.java
        ).build()
        dao = db.recipeDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetRecipe() = runBlocking {
        val recipe = RecipeEntity(
            id = "1",
            title = "Test Recipe",
            description = "Desc",
            portions = 4,
            ingredients = listOf(Ingredient("Test", 1.0, "Unit")),
            steps = listOf("Step 1"),
            photos = emptyList(),
            creationDate = 12345L
        )
        
        dao.insertRecipe(recipe)
        val byId = dao.getRecipeById("1")
        assertEquals(byId?.title, "Test Recipe")
        
        val list = dao.getRecipes().first()
        assertEquals(1, list.size)
    }
}
