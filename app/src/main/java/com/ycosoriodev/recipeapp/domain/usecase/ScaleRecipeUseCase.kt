package com.ycosoriodev.recipeapp.domain.usecase

import com.ycosoriodev.recipeapp.domain.model.Recipe

class ScaleRecipeUseCase {
    operator fun invoke(recipe: Recipe, factor: Float): Recipe {
        if (factor <= 0) return recipe
        
        val newIngredients = recipe.ingredients.map { ingredient ->
            ingredient.copy(amount = ingredient.amount * factor)
        }
        
        // Scale portions as well, rounding to nearest integer if needed
        val newPortions = (recipe.portions * factor).toInt().coerceAtLeast(1)

        return recipe.copy(
            ingredients = newIngredients,
            portions = newPortions
        )
    }
}
