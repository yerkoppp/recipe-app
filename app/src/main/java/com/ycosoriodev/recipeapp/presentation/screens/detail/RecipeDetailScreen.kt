package com.ycosoriodev.recipeapp.presentation.screens.detail

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.ycosoriodev.recipeapp.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    navController: NavController,
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState().value
    val context = LocalContext.current

    if (state.isDeleted) {
        androidx.compose.runtime.LaunchedEffect(Unit) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.recipe?.title ?: "Detalle") },
                actions = {
                    IconButton(onClick = { 
                        state.recipe?.let { 
                            // Since we don't have direct access to ViewModel toggle here easily without adding it to DetailViewModel, 
                            // we'll rely on the list for now or assuming DetailViewModel updates.
                            // Better: Add toggleFavorite to RecipeDetailViewModel
                            viewModel.onEvent(RecipeDetailEvent.ToggleFavorite)
                        } 
                    }) {
                        val isFav = state.recipe?.isFavorite == true
                        Icon(
                            if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder, 
                            contentDescription = "Favorito",
                            tint = if (isFav) androidx.compose.ui.graphics.Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = {
                        state.recipe?.let { recipe ->
                             val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, formatRecipeForShare(recipe))
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Compartir receta")
                            context.startActivity(shareIntent)
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir")
                    }
                    IconButton(onClick = {
                         state.recipe?.let { recipe ->
                            navController.navigate(Screen.EditRecipe.createRoute(recipe.id))
                        }
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { viewModel.onEvent(RecipeDetailEvent.DeleteRecipe) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        val recipe = state.recipe
        if (recipe == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Description
                item {
                   Text(text = recipe.description, style = MaterialTheme.typography.bodyLarge)
                }

                // Scaling Control
                item {
                    Text("Escalar porciones:", style = MaterialTheme.typography.labelLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(0.5f, 1.0f, 2.0f, 3.0f).forEach { scale ->
                            FilterChip(
                                selected = state.currentScale == scale,
                                onClick = { viewModel.onEvent(RecipeDetailEvent.ChangeScale(scale)) },
                                label = { Text("x${scale}") }
                            )
                        }
                         FilterChip(
                            selected = false,
                            onClick = { viewModel.onEvent(RecipeDetailEvent.ShowCustomScaleDialog) },
                            label = { Text("Otro") }
                        )
                    }
                    Text(
                        "${recipe.portions} Porciones", 
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Ingredients
                item {
                    Text("Ingredientes", style = MaterialTheme.typography.titleLarge)
                }
                items(recipe.ingredients) { ingredient ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "• ${ingredient.name}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "${String.format("%.1f", ingredient.amount)} ${ingredient.unit}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        }
                    }
                }

                // Steps
                item {
                    Text("Preparación", style = MaterialTheme.typography.titleLarge)
                }
                items(recipe.steps.size) { index ->
                    Row(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            "${index + 1}.",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.width(32.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            recipe.steps[index],
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                // Photos Carousel (Moved to bottom)
                if (recipe.photos.isNotEmpty()) {
                    item {
                        Text("Fotos", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(recipe.photos) { photo ->
                                Image(
                                    painter = rememberAsyncImagePainter(model = photo),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Clone Confirmation Dialog
    if (state.showCloneDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(RecipeDetailEvent.DismissCloneDialog) },
            title = { Text("Clonar Receta") },
            text = { Text("¿Quieres crear una copia de esta receta?") },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(RecipeDetailEvent.DuplicateRecipe) }) {
                    Text("Clonar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(RecipeDetailEvent.DismissCloneDialog) }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Custom Scale Dialog
    if (state.showCustomScaleDialog) {
        var customFactorText by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(RecipeDetailEvent.DismissCustomScaleDialog) },
            title = { Text("Escala Personalizada") },
            text = {
                OutlinedTextField(
                    value = customFactorText,
                    onValueChange = { customFactorText = it },
                    label = { Text("Multiplicador (ej. 1.5)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal)
                )
            },
            confirmButton = {
                TextButton(onClick = { 
                    val factor = customFactorText.toFloatOrNull()
                    if (factor != null && factor > 0) {
                        viewModel.onEvent(RecipeDetailEvent.ChangeScale(factor))
                        viewModel.onEvent(RecipeDetailEvent.DismissCustomScaleDialog)
                    }
                }) {
                    Text("Aplicar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(RecipeDetailEvent.DismissCustomScaleDialog) }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

fun formatRecipeForShare(recipe: com.ycosoriodev.recipeapp.domain.model.Recipe): String {
    val sb = StringBuilder()
    sb.append("${recipe.title}\n\n")
    if (recipe.description.isNotBlank()) sb.append("${recipe.description}\n\n")
    sb.append("Ingredientes (${recipe.portions} porciones):\n")
    recipe.ingredients.forEach { 
        sb.append("- ${it.name}: ${it.amount} ${it.unit}\n")
    }
    sb.append("\nPreparación:\n")
    recipe.steps.forEachIndexed { index, step ->
        sb.append("${index + 1}. $step\n")
    }
    return sb.toString()
}
