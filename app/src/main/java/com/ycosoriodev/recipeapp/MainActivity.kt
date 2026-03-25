package com.ycosoriodev.recipeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ycosoriodev.recipeapp.presentation.theme.RecipeAppTheme
import dagger.hilt.android.AndroidEntryPoint
import com.ycosoriodev.recipeapp.presentation.navigation.RecipeNavGraph

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecipeAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   RecipeNavGraph(navController = androidx.navigation.compose.rememberNavController())
                }
            }
        }
    }
}
