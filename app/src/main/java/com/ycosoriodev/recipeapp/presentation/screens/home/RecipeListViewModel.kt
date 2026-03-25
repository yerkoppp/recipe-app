package com.ycosoriodev.recipeapp.presentation.screens.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ycosoriodev.recipeapp.domain.model.Recipe
import com.ycosoriodev.recipeapp.domain.usecase.RecipeUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val recipeUseCases: RecipeUseCases
) : ViewModel() {

    private val _recipes = mutableStateOf<List<Recipe>>(emptyList())
    val recipes: State<List<Recipe>> = _recipes
    
    // Simple query string for filtering
    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery
    
    private val _selectedCategory = mutableStateOf<String?>(null)
    val selectedCategory: State<String?> = _selectedCategory

    private var getRecipesJob: Job? = null

    init {
        getRecipes()
    }

    private fun getRecipes() {
        getRecipesJob?.cancel()
        getRecipesJob = recipeUseCases.getRecipes()
            .onEach { recipes ->
                _recipes.value = recipes
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
    
    fun onCategorySelect(category: String?) {
        _selectedCategory.value = category
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeUseCases.deleteRecipe(recipe)
        }
    }

    fun cloneRecipe(recipe: Recipe) {
        viewModelScope.launch {
            val clonedRecipe = recipe.copy(
                id = java.util.UUID.randomUUID().toString(),
                title = "Copia de ${recipe.title}",
                creationDate = System.currentTimeMillis(),
                isFavorite = false // Clones shouldn't inherit favorite status automatically, or maybe they should? false is safer.
            )
            recipeUseCases.addRecipe(clonedRecipe)
        }
    }
    
    private val _showFavoritesOnly = mutableStateOf(false)
    val showFavoritesOnly: State<Boolean> = _showFavoritesOnly
    
    fun onToggleFavoriteFilter() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
    }
    
    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            recipeUseCases.addRecipe(recipe.copy(isFavorite = !recipe.isFavorite))
        }
    }
}
