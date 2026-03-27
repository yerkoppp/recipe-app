package com.ycosoriodev.recipeapp.presentation.screens.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ycosoriodev.recipeapp.data.local.RecipeEntity
import com.ycosoriodev.recipeapp.data.local.toDomain
import com.ycosoriodev.recipeapp.data.local.toEntity
import com.ycosoriodev.recipeapp.data.repository.RecipeRepositoryImpl
import com.ycosoriodev.recipeapp.domain.model.Recipe
import com.ycosoriodev.recipeapp.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: RecipeRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    fun exportDataToUri(uri: Uri) {
        viewModelScope.launch {
            try {
                // Get all recipes.
                val recipes = repository.getRecipes().first()
                val recipesJson = json.encodeToString(recipes.map { it.toEntity() })
                
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(recipesJson.toByteArray())
                }
                android.widget.Toast.makeText(context, "Archivo exportado correctamente", android.widget.Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                android.widget.Toast.makeText(context, "Error al exportar: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }

    fun importData(uri: Uri) {
        viewModelScope.launch {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val jsonString = inputStream.bufferedReader().use { it.readText() }
                    val entities = json.decodeFromString<List<RecipeEntity>>(jsonString)
                    
                    // Restore
                    entities.forEach { entity ->
                        repository.insertRecipe(entity.toDomain())
                    }
                    android.widget.Toast.makeText(context, "Recetas importadas correctamente", android.widget.Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                android.widget.Toast.makeText(context, "Error al importar: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }

    fun exportDataForSharing(onFileReady: (File) -> Unit) {
        viewModelScope.launch {
            try {
                val recipes = repository.getRecipes().first()
                val recipesJson = json.encodeToString(recipes.map { it.toEntity() })

                // Guarda temporalmente en caché
                val file = File(context.cacheDir, "recetas_backup.json")
                file.writeText(recipesJson)

                onFileReady(file)
            } catch (e: Exception) {
                e.printStackTrace()
                android.widget.Toast.makeText(context, "Error al exportar: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }
}
