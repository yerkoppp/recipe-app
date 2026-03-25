package com.ycosoriodev.recipeapp.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object EditRecipe : Screen("edit_recipe_screen?recipeId={recipeId}") {
        fun createRoute(recipeId: String? = null) = "edit_recipe_screen?recipeId=${recipeId ?: ""}"
    }
    object RecipeDetail : Screen("recipe_detail_screen/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe_detail_screen/$recipeId"
    }
    object Settings : Screen("settings_screen")
}
