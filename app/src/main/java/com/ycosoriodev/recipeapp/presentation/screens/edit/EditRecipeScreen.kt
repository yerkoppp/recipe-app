package com.ycosoriodev.recipeapp.presentation.screens.edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeScreen(
    navController: NavController,
    viewModel: EditRecipeViewModel = hiltViewModel()
) {
    val snackbarHostState = androidx.compose.runtime.remember { SnackbarHostState() }
    val showAddCategoryDialog = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is EditRecipeViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is EditRecipeViewModel.UiEvent.SaveRecipe -> {
                    navController.navigateUp()
                }
            }
        }
    }
    
     val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { viewModel.onEvent(EditRecipeEvent.AddPhoto(it)) }
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Editar Receta") },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(EditRecipeEvent.SaveRecipe) }) {
                        Icon(Icons.Default.Check, contentDescription = "Guardar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Basic Info
            item {
                OutlinedTextField(
                    value = viewModel.title.value,
                    onValueChange = { viewModel.onEvent(EditRecipeEvent.EnteredTitle(it)) },
                    label = { Text("Título de la receta") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Categories
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Categorías", style = MaterialTheme.typography.labelLarge)
                    androidx.compose.material3.TextButton(onClick = { showAddCategoryDialog.value = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Crear")
                    }
                }
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.availableCategories) { category ->
                         FilterChip(
                            selected = viewModel.selectedCategories.contains(category),
                            onClick = { viewModel.onEvent(EditRecipeEvent.ToggleCategory(category)) },
                            label = { Text(category) },
                            leadingIcon = if (viewModel.selectedCategories.contains(category)) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = viewModel.description.value,
                    onValueChange = { viewModel.onEvent(EditRecipeEvent.EnteredDescription(it)) },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.portions.value,
                    onValueChange = { viewModel.onEvent(EditRecipeEvent.EnteredPortions(it)) },
                    label = { Text("Porciones") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Ingredients
            item {
                Text("Ingredientes", style = MaterialTheme.typography.titleMedium)
            }
            items(viewModel.ingredients.size) { index ->
                val ingredient = viewModel.ingredients[index]
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        OutlinedTextField(
                            value = ingredient.name,
                            onValueChange = {
                                viewModel.onEvent(
                                    EditRecipeEvent.ChangeIngredientName(
                                        index,
                                        it
                                    )
                                )
                            },
                            label = { Text("Nombre del Ingrediente") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = if (ingredient.amount == 0.0) "" else ingredient.amount.toString(),
                                onValueChange = {
                                    viewModel.onEvent(
                                        EditRecipeEvent.ChangeIngredientAmount(
                                            index,
                                            it
                                        )
                                    )
                                },
                                label = { Text("Cantidad") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = ingredient.unit,
                                onValueChange = {
                                    viewModel.onEvent(
                                        EditRecipeEvent.ChangeIngredientUnit(
                                            index,
                                            it
                                        )
                                    )
                                },
                                label = { Text("Unidad") },
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                viewModel.onEvent(
                                    EditRecipeEvent.RemoveIngredient(
                                        index
                                    )
                                )
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Borrar Ingrediente",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
            item {
                Button(onClick = { viewModel.onEvent(EditRecipeEvent.AddIngredient) }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("Agregar Ingrediente")
                }
            }

            // Steps
            item {
                Text("Pasos de Preparación", style = MaterialTheme.typography.titleMedium)
            }
            items(viewModel.steps.size) { index ->
                val step = viewModel.steps[index]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${index + 1}.", modifier = Modifier.padding(end = 8.dp))
                    OutlinedTextField(
                        value = step,
                        onValueChange = {
                            viewModel.onEvent(
                                EditRecipeEvent.ChangeStep(
                                    index,
                                    it
                                )
                            )
                        },
                        label = { Text("Descripción del paso") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.onEvent(EditRecipeEvent.RemoveStep(index)) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Borrar Paso")
                    }
                }
            }
            item {
                Button(onClick = { viewModel.onEvent(EditRecipeEvent.AddStep) }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("Agregar Paso")
                }
            }

            // Photos (Moved to bottom)
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )
                Text("Fotos (Máx 4)", style = MaterialTheme.typography.titleMedium)
                LazyRow(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.photos) { photoPath ->
                        Box(modifier = Modifier.size(100.dp)) {
                            Image(
                                painter = rememberAsyncImagePainter(model = photoPath),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            IconButton(
                                onClick = { viewModel.onEvent(EditRecipeEvent.RemovePhoto(photoPath)) },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                    if (viewModel.photos.size < 4) {
                        item {
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                },
                                modifier = Modifier.size(100.dp),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Agregar foto")
                            }
                        }
                    }
                }
            }
        }
        }

    
    if (showAddCategoryDialog.value) {
        val newCategoryName = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showAddCategoryDialog.value = false },
            title = { Text("Nueva Categoría") },
            text = {
                OutlinedTextField(
                    value = newCategoryName.value,
                    onValueChange = { newCategoryName.value = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        if (newCategoryName.value.isNotBlank()) {
                            viewModel.onEvent(EditRecipeEvent.AddNewCategory(newCategoryName.value))
                            showAddCategoryDialog.value = false
                        }
                    }
                ) {
                    Text("Agregar")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showAddCategoryDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
