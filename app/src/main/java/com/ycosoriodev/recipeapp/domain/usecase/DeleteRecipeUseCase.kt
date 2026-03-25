package com.ycosoriodev.recipeapp.domain.usecase

import com.ycosoriodev.recipeapp.domain.model.Recipe
import com.ycosoriodev.recipeapp.domain.repository.RecipeRepository

class DeleteRecipeUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipe: Recipe) {
        repository.deleteRecipe(recipe)
    }
}
