package com.ycosoriodev.recipeapp.presentation.screens.edit

import android.net.Uri

sealed class EditRecipeEvent {
    data class EnteredTitle(val value: String) : EditRecipeEvent()
    data class EnteredDescription(val value: String) : EditRecipeEvent()
    data class EnteredPortions(val value: String) : EditRecipeEvent()
    
    data class ToggleCategory(val category: String) : EditRecipeEvent()
    data class AddNewCategory(val category: String) : EditRecipeEvent()
    
    object AddIngredient : EditRecipeEvent()
    data class ChangeIngredientName(val index: Int, val value: String) : EditRecipeEvent()
    data class ChangeIngredientAmount(val index: Int, val value: String) : EditRecipeEvent()
    data class ChangeIngredientUnit(val index: Int, val value: String) : EditRecipeEvent()
    data class RemoveIngredient(val index: Int) : EditRecipeEvent()
    
    object AddStep : EditRecipeEvent()
    data class ChangeStep(val index: Int, val value: String) : EditRecipeEvent()
    data class RemoveStep(val index: Int) : EditRecipeEvent()
    
    data class AddPhoto(val uri: Uri) : EditRecipeEvent()
    data class RemovePhoto(val uri: String) : EditRecipeEvent()
    
    object SaveRecipe : EditRecipeEvent()
}
