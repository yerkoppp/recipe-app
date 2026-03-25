package com.ycosoriodev.recipeapp.data.repository

import com.ycosoriodev.recipeapp.data.local.RecipeDao
import com.ycosoriodev.recipeapp.data.local.toDomain
import com.ycosoriodev.recipeapp.data.local.toEntity
import com.ycosoriodev.recipeapp.domain.model.Recipe
import com.ycosoriodev.recipeapp.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeRepositoryImpl(
    private val dao: RecipeDao
) : RecipeRepository {
    override fun getRecipes(): Flow<List<Recipe>> {
        return dao.getRecipes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getRecipeById(id: String): Recipe? {
        return dao.getRecipeById(id)?.toDomain()
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        dao.insertRecipe(recipe.toEntity())
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        dao.deleteRecipe(recipe.toEntity())
    }
    
    override suspend fun updateRecipe(recipe: Recipe) {
        dao.updateRecipe(recipe.toEntity())
    }
}
