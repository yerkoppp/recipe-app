package com.ycosoriodev.recipeapp.presentation.screens.edit

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ycosoriodev.recipeapp.domain.model.Ingredient
import com.ycosoriodev.recipeapp.domain.model.Recipe
import com.ycosoriodev.recipeapp.domain.usecase.RecipeUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditRecipeViewModel @Inject constructor(
    private val recipeUseCases: RecipeUseCases,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val recipeId: String? = savedStateHandle.get<String>("recipeId")?.let { 
        if(it.isBlank() || it == "null") null else it 
    }

    var title = androidx.compose.runtime.mutableStateOf("")
    var description = androidx.compose.runtime.mutableStateOf("")
    var portions = androidx.compose.runtime.mutableStateOf("1")
    
    // Available categories (defaults + any new ones)
    private val _availableCategories = androidx.compose.runtime.mutableStateListOf(
        "Sin categoría", "Entrada", "Fondo", "Postre", "Dulces", "Panes", "Vegetarianos", "Bebestible", "Snack"
    )
    val availableCategories: List<String> get() = _availableCategories

    // Selected categories
    private val _selectedCategories = androidx.compose.runtime.mutableStateListOf<String>()
    val selectedCategories: List<String> get() = _selectedCategories
    
    // Using simple MutableState lists for dynamic fields
    private val _ingredients = androidx.compose.runtime.mutableStateListOf<Ingredient>()
    val ingredients: List<Ingredient> get() = _ingredients
    
    private val _steps = androidx.compose.runtime.mutableStateListOf<String>()
    val steps: List<String> get() = _steps
    
    private val _photos = androidx.compose.runtime.mutableStateListOf<String>()
    val photos: List<String> get() = _photos

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        if (recipeId != null) {
            viewModelScope.launch {
                recipeUseCases.getRecipeById(recipeId)?.let { recipe ->
                    title.value = recipe.title
                    description.value = recipe.description
                    portions.value = recipe.portions.toString()
                    _selectedCategories.clear()
                    _selectedCategories.addAll(recipe.categories)
                    _ingredients.clear()
                    _ingredients.addAll(recipe.ingredients)
                    _steps.clear()
                    _steps.addAll(recipe.steps)
                    _photos.clear()
                    _photos.addAll(recipe.photos)
                }
            }
        } else {
            // Default initial state
            _ingredients.add(Ingredient("", 0.0, "Gramos"))
            _steps.add("")
        }
    }

    fun onEvent(event: EditRecipeEvent) {
        when(event) {
            is EditRecipeEvent.EnteredTitle -> title.value = event.value
            is EditRecipeEvent.EnteredDescription -> description.value = event.value
            is EditRecipeEvent.EnteredPortions -> portions.value = event.value
            
            is EditRecipeEvent.ToggleCategory -> {
                if (_selectedCategories.contains(event.category)) {
                    _selectedCategories.remove(event.category)
                } else {
                    _selectedCategories.add(event.category)
                }
            }
            is EditRecipeEvent.AddNewCategory -> {
                if (event.category.isNotBlank() && !_availableCategories.contains(event.category)) {
                    _availableCategories.add(event.category)
                    _selectedCategories.add(event.category)
                }
            }
            
            is EditRecipeEvent.AddIngredient -> {
                _ingredients.add(Ingredient("", 0.0, "Gramos"))
            }
            is EditRecipeEvent.ChangeIngredientName -> {
                _ingredients[event.index] = _ingredients[event.index].copy(name = event.value)
            }
            is EditRecipeEvent.ChangeIngredientAmount -> {
                _ingredients[event.index] = _ingredients[event.index].copy(amount = event.value.toDoubleOrNull() ?: 0.0)
            }
            is EditRecipeEvent.ChangeIngredientUnit -> {
                 _ingredients[event.index] = _ingredients[event.index].copy(unit = event.value)
            }
            is EditRecipeEvent.RemoveIngredient -> {
                if (_ingredients.size > 0) _ingredients.removeAt(event.index)
            }
            
            is EditRecipeEvent.AddStep -> {
                _steps.add("")
            }
            is EditRecipeEvent.ChangeStep -> {
                _steps[event.index] = event.value
            }
            is EditRecipeEvent.RemoveStep -> {
                if(_steps.size > 0) _steps.removeAt(event.index)
            }
            
            is EditRecipeEvent.AddPhoto -> {
                // Copy uri to internal storage
                savePhotoToInternal(event.uri)
            }
            is EditRecipeEvent.RemovePhoto -> {
                _photos.remove(event.uri)
            }
            
            is EditRecipeEvent.SaveRecipe -> {
                viewModelScope.launch {
                    try {
                        val finalPortions = portions.value.toIntOrNull() ?: 1
                        if (title.value.isBlank()) {
                            _eventFlow.emit(UiEvent.ShowSnackbar("El título no puede estar vacío"))
                            return@launch
                        }
                        
                        recipeUseCases.addRecipe(
                            Recipe(
                                id = recipeId ?: UUID.randomUUID().toString(),
                                title = title.value,
                                description = description.value,
                                portions = finalPortions,
                                ingredients = _ingredients.filter { it.name.isNotBlank() },
                                steps = _steps.filter { it.isNotBlank() },
                                photos = _photos,
                                creationDate = System.currentTimeMillis(),
                                categories = _selectedCategories
                            )
                        )
                        _eventFlow.emit(UiEvent.SaveRecipe)
                    } catch (e: Exception) {
                        _eventFlow.emit(UiEvent.ShowSnackbar(e.message ?: "Error desconocido"))
                    }
                }
            }
        }
    }

    private fun savePhotoToInternal(uri: Uri) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val fileName = "recipe_${UUID.randomUUID()}.jpg"
                val file = File(context.filesDir, "images")
                if (!file.exists()) file.mkdirs()
                val destFile = File(file, fileName)
                val outputStream = FileOutputStream(destFile)
                
                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                _photos.add(destFile.absolutePath)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveRecipe : UiEvent()
    }
}
