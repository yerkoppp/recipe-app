package com.ycosoriodev.recipeapp.domain.usecase

import com.ycosoriodev.recipeapp.domain.model.Recipe
import com.ycosoriodev.recipeapp.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

class GetRecipesUseCase(
    private val repository: RecipeRepository
) {
    operator fun invoke(): Flow<List<Recipe>> {
        return repository.getRecipes()
    }
}
