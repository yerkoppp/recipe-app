package com.ycosoriodev.recipeapp.domain.usecase

import com.ycosoriodev.recipeapp.domain.model.Recipe
import com.ycosoriodev.recipeapp.domain.repository.RecipeRepository

class AddRecipeUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipe: Recipe) {
        if (recipe.title.isBlank()) {
            throw IllegalArgumentException("The title of the recipe cannot be empty.")
        }
        repository.insertRecipe(recipe)
    }
}
