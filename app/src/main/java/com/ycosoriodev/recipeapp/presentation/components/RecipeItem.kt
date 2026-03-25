package com.ycosoriodev.recipeapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ycosoriodev.recipeapp.domain.model.Recipe
import com.ycosoriodev.recipeapp.R
import java.io.File

@Composable
fun RecipeItem(
    recipe: Recipe,
    onClick: () -> Unit,
    onClone: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail (First photo or placeholder)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (recipe.photos.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = recipe.photos.first()),
                        contentDescription = "Foto de ${recipe.title}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.not_photo),
                        contentDescription = "Foto de ${recipe.title}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    /*Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Sin imagen",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )*/
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (recipe.description.isNotBlank()) {
                     Text(
                        text = recipe.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Quick stats
                val categoryText = if (recipe.categories.isNotEmpty()) recipe.categories.joinToString(", ") else "Sin categoría"
                Text(
                    text = "$categoryText • ${recipe.portions} porciones",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (recipe.isFavorite) androidx.compose.material.icons.Icons.Default.Favorite else androidx.compose.material.icons.Icons.Default.FavoriteBorder,
                    contentDescription = if (recipe.isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                    tint = if (recipe.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = onClone) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Add, // Using Add as placeholder or if ContentCopy not imported.
                    // Better to use a specific icon if available. I will rely on standard icons.
                    // Actually, I'll use standard Icons.Default.Add for now and fix import if I can't find Copy easily without import check.
                    // Wait, I can try Icons.Default.ContentCopy assuming it exists.
                    contentDescription = "Clonar"
                )
            }
        }
    }
}
