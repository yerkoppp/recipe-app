package com.ycosoriodev.recipeapp.domain.usecase

import com.ycosoriodev.recipeapp.domain.model.Recipe
import com.ycosoriodev.recipeapp.domain.repository.RecipeRepository

class GetRecipeByIdUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(id: String): Recipe? {
        return repository.getRecipeById(id)
    }
}
