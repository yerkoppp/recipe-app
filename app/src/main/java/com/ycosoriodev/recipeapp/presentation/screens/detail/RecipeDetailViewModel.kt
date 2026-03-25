package com.ycosoriodev.recipeapp.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ycosoriodev.recipeapp.domain.model.Recipe
import com.ycosoriodev.recipeapp.domain.usecase.RecipeUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val recipeUseCases: RecipeUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recipeId: String? = savedStateHandle.get<String>("recipeId")
    
    private val _state = MutableStateFlow<RecipeDetailState>(RecipeDetailState())
    val state: StateFlow<RecipeDetailState> = _state

    private var baseRecipe: Recipe? = null

    init {
        recipeId?.let { id ->
            viewModelScope.launch {
                recipeUseCases.getRecipeById(id)?.let { recipe ->
                    baseRecipe = recipe
                    _state.value = _state.value.copy(recipe = recipe)
                }
            }
        }
    }

    fun onEvent(event: RecipeDetailEvent) {
        when(event) {
            is RecipeDetailEvent.ChangeScale -> {
                baseRecipe?.let { base ->
                    val scaledRecipe = recipeUseCases.scaleRecipe(base, event.factor)
                    _state.value = _state.value.copy(
                        recipe = scaledRecipe,
                        currentScale = event.factor
                    )
                }
            }
            is RecipeDetailEvent.ShowCloneDialog -> {
                _state.value = _state.value.copy(showCloneDialog = true)
            }
            is RecipeDetailEvent.DismissCloneDialog -> {
                _state.value = _state.value.copy(showCloneDialog = false)
            }
            is RecipeDetailEvent.ShowCustomScaleDialog -> {
                _state.value = _state.value.copy(showCustomScaleDialog = true)
            }
            is RecipeDetailEvent.DismissCustomScaleDialog -> {
                _state.value = _state.value.copy(showCustomScaleDialog = false)
            }
            is RecipeDetailEvent.DuplicateRecipe -> {
                baseRecipe?.let { base ->
                    viewModelScope.launch {
                        val copy = base.copy(
                            id = UUID.randomUUID().toString(),
                            title = "${base.title} (Copia)",
                            creationDate = System.currentTimeMillis()
                        )
                        recipeUseCases.addRecipe(copy)
                        _state.value = _state.value.copy(showCloneDialog = false) // Close after action
                    }
                }
            }
            is RecipeDetailEvent.DeleteRecipe -> {
                baseRecipe?.let {
                    viewModelScope.launch {
                        recipeUseCases.deleteRecipe(it)
                        _state.value = _state.value.copy(isDeleted = true)
                    }
                }
            }
            is RecipeDetailEvent.ToggleFavorite -> {
                 baseRecipe?.let { recipe ->
                    viewModelScope.launch {
                        val updatedRecipe = recipe.copy(isFavorite = !recipe.isFavorite)
                        recipeUseCases.addRecipe(updatedRecipe)
                        baseRecipe = updatedRecipe
                        _state.value = _state.value.copy(recipe = updatedRecipe)
                    }
                 }
            }
        }
    }
}

data class RecipeDetailState(
    val recipe: Recipe? = null,
    val currentScale: Float = 1.0f,
    val isDeleted: Boolean = false,
    val showCloneDialog: Boolean = false,
    val showCustomScaleDialog: Boolean = false
)

sealed class RecipeDetailEvent {
    data class ChangeScale(val factor: Float) : RecipeDetailEvent()
    object DuplicateRecipe : RecipeDetailEvent()
    object ShowCloneDialog : RecipeDetailEvent()
    object DismissCloneDialog : RecipeDetailEvent()
    object ShowCustomScaleDialog : RecipeDetailEvent()
    object DismissCustomScaleDialog : RecipeDetailEvent()
    object DeleteRecipe : RecipeDetailEvent()
    object ToggleFavorite : RecipeDetailEvent()
}
