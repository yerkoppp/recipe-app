package com.ycosoriodev.recipeapp.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ycosoriodev.recipeapp.R
import com.ycosoriodev.recipeapp.presentation.components.RecipeItem
import com.ycosoriodev.recipeapp.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    navController: NavController,
    viewModel: RecipeListViewModel = hiltViewModel()
) {
    val recipesLines = viewModel.recipes.value
    val searchQuery = viewModel.searchQuery.value
    val selectedCategory = viewModel.selectedCategory.value
    val showFavoritesOnly = viewModel.showFavoritesOnly.value
    
    val showDeleteDialog = remember { mutableStateOf(false) }
    val recipeToDelete = remember { mutableStateOf<com.ycosoriodev.recipeapp.domain.model.Recipe?>(null) }
    
    val showCloneDialog = remember { mutableStateOf(false) }
    val recipeToClone = remember { mutableStateOf<com.ycosoriodev.recipeapp.domain.model.Recipe?>(null) }

    // Filter logic
    val recipes = recipesLines.filter { recipe ->
        val matchesSearch = if (searchQuery.isBlank()) true else recipe.title.contains(searchQuery, ignoreCase = true)
        val matchesCategory = if (selectedCategory == null) true else recipe.categories.contains(selectedCategory)
        val matchesFavorite = if (showFavoritesOnly) recipe.isFavorite else true
        matchesSearch && matchesCategory && matchesFavorite
    }
    
    // Extract all unique categories from recipes logic
    val allCategories = remember(recipesLines) {
        recipesLines.flatMap { it.categories }.distinct().sorted()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_home)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Configuración", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.EditRecipe.createRoute(null))
                },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_add))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar receta...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            
            // Category Filter
            if (allCategories.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                         FilterChip(
                            selected = showFavoritesOnly,
                            onClick = { viewModel.onToggleFavoriteFilter() },
                            leadingIcon = { 
                                Icon(
                                    if (showFavoritesOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder, 
                                    contentDescription = null,
                                    tint = if (showFavoritesOnly) MaterialTheme.colorScheme.primary else LocalContentColor.current
                                ) 
                            },
                            label = { Text("Favoritos") }
                        )
                    }
                    item {
                         FilterChip(
                            selected = selectedCategory == null,
                            onClick = { viewModel.onCategorySelect(null) },
                            label = { Text("Todos") }
                        )
                    }
                    items(allCategories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { viewModel.onCategorySelect(if (selectedCategory == category) null else category) },
                            label = { Text(category) }
                        )
                    }
                }
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    items = recipes,
                    key = { it.id }
                ) { recipe ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                recipeToDelete.value = recipe
                                showDeleteDialog.value = true
                                false // Don't dismiss immediately, wait for dialog
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val color = MaterialTheme.colorScheme.errorContainer
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        },
                        content = {
                            RecipeItem(
                                recipe = recipe,
                                onClick = {
                                    navController.navigate(Screen.RecipeDetail.createRoute(recipe.id))
                                },
                                onClone = {
                                    recipeToClone.value = recipe
                                    showCloneDialog.value = true
                                },
                                onToggleFavorite = {
                                    viewModel.toggleFavorite(recipe)
                                }
                            )
                        }
                    )
                }
            }
            
            if (showDeleteDialog.value && recipeToDelete.value != null) {
                AlertDialog(
                    onDismissRequest = { 
                        showDeleteDialog.value = false 
                        recipeToDelete.value = null
                    },
                    title = { Text("Eliminar Receta") },
                    text = { Text("¿Estás seguro que deseas eliminar '${recipeToDelete.value?.title}'?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                recipeToDelete.value?.let { viewModel.deleteRecipe(it) }
                                showDeleteDialog.value = false
                                recipeToDelete.value = null
                            }
                        ) {
                            Text("Eliminar")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { 
                                showDeleteDialog.value = false 
                                recipeToDelete.value = null
                            }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            
            if (showCloneDialog.value && recipeToClone.value != null) {
                AlertDialog(
                    onDismissRequest = { 
                        showCloneDialog.value = false 
                        recipeToClone.value = null
                    },
                    title = { Text("Clonar Receta") },
                    text = { Text("¿Deseas crear una copia de '${recipeToClone.value?.title}'?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                recipeToClone.value?.let { viewModel.cloneRecipe(it) }
                                showCloneDialog.value = false
                                recipeToClone.value = null
                            }
                        ) {
                            Text("Clonar")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { 
                                showCloneDialog.value = false 
                                recipeToClone.value = null
                            }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}
