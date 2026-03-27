package com.ycosoriodev.recipeapp.presentation.screens.settings

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let { viewModel.importData(it) }
        }
    )

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = { uri ->
            uri?.let { viewModel.exportDataToUri(it) }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
             modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
             verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Gestión de Datos", style = MaterialTheme.typography.titleMedium)

            Button(
                onClick = {
                    viewModel.exportDataForSharing { file ->
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider", // Usa el authority definido en tu AndroidManifest
                            file
                        )
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/json"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Compartir recetas"))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Compartir Exportación (JSON)")
            }

            Button(
                onClick = {
                    importLauncher.launch(arrayOf("application/json"))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Importar Recetas (JSON)")
            }

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            Text("Accesibilidad", style = MaterialTheme.typography.titleMedium)
            Text("Estas opciones están controladas por la configuración del sistema.", style = MaterialTheme.typography.bodyMedium)
            
            ListItem(
                headlineContent = { Text("Tamaño de texto") },
                supportingContent = { Text("Ajustar en Configuración de Android > Accesibilidad") }
            )
             ListItem(
                headlineContent = { Text("Tema Oscuro") },
                supportingContent = { Text("Ajustar en Configuración de Android > Pantalla") }
            )
        }
    }
}


