package com.ycosoriodev.recipeapp.domain.usecase

data class RecipeUseCases(
    val getRecipes: GetRecipesUseCase,
    val getRecipeById: GetRecipeByIdUseCase,
    val addRecipe: AddRecipeUseCase,
    val deleteRecipe: DeleteRecipeUseCase,
    val scaleRecipe: ScaleRecipeUseCase
)
