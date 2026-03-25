package com.ycosoriodev.recipeapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.ycosoriodev.recipeapp.presentation.screens.home.RecipeListScreen
import com.ycosoriodev.recipeapp.presentation.screens.edit.EditRecipeScreen
import com.ycosoriodev.recipeapp.presentation.screens.detail.RecipeDetailScreen

@Composable
fun RecipeNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            RecipeListScreen(navController = navController)
        }
        composable(
            route = Screen.EditRecipe.route,
            arguments = listOf(
                navArgument("recipeId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            EditRecipeScreen(navController = navController)
        }
        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(
                navArgument("recipeId") {
                    type = NavType.StringType
                }
            )
        ) {
            RecipeDetailScreen(navController = navController)
        }
        composable(route = Screen.Settings.route) {
            com.ycosoriodev.recipeapp.presentation.screens.settings.SettingsScreen(navController = navController)
        }
    }
}
