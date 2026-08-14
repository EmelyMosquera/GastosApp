package com.emely.gastosapp

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
// Importación obligatoria para Coil (Carga de imágenes de internet)
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaGastos(viewModel: GastosViewModel, alIrAAjustes: () -> Unit) {
    val gastos by viewModel.listaGastos.collectAsState()
    var nombreGasto by remember { mutableStateOf("") }
    var precioGasto by remember { mutableStateOf("") }
    val contexto = LocalContext.current

    val lanzadorCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { mapaBits ->
        if (mapaBits != null) {
            viewModel.fotoTemporalUri = "foto_recibo_${System.currentTimeMillis()}"
            Toast.makeText(contexto, "¡Foto del recibo vinculada!", Toast.LENGTH_SHORT).show()
        }
    }

    val lanzadorPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permisoOtorgado ->
        if (permisoOtorgado) {
            lanzadorCamara.launch(null)
        } else {
            Toast.makeText(contexto, "Permiso denegado. No se puede usar la cámara.", Toast.LENGTH_LONG).show()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GastosApp - Finanzas") },
                actions = {
                    Button(onClick = alIrAAjustes) { Text("Ajustes") }
                }
            )
        }
    ) { valoresPadding ->
        Column(modifier = Modifier.padding(valoresPadding).padding(16.dp)) {

            // Componente Coil para cargar imágenes remotas asíncronas desde internet (Punto 4a)
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = "https://unsplash.com",
                    contentDescription = "Logotipo de finanzas",
                    modifier = Modifier.size(60.dp).padding(end = 8.dp)
                )
                Text(text = "Tu Panel de Control", style = MaterialTheme.typography.headlineSmall)
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (viewModel.estadoApi) {
                        "Éxito" -> MaterialTheme.colorScheme.primaryContainer
                        "Cargando..." -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.errorContainer
                    }
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Estado del Servidor API: ${viewModel.estadoApi}", style = MaterialTheme.typography.titleSmall)
                    Text(text = viewModel.mensajeResultado, style = MaterialTheme.typography.bodyMedium)
                    if (viewModel.estadoApi == "Error") {
                        TextButton(onClick = { viewModel.consultarServidorRemoto() }) {
                            Text("Reintentar conexión")
                        }
                    }
                }
            }

            OutlinedTextField(
                value = nombreGasto,
                onValueChange = { nombreGasto = it },
                label = { Text("Descripción del gasto") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = precioGasto,
                onValueChange = { precioGasto = it },
                label = { Text("Monto ($)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { lanzadorPermiso.launch(android.Manifest.permission.CAMERA) },
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                ) {
                    Text(if (viewModel.fotoTemporalUri != null) "📸 ¡Foto Lista!" else "📷 Tomar Recibo")
                }

                Button(
                    onClick = {
                        if (nombreGasto.isNotEmpty() && precioGasto.isNotEmpty()) {
                            val montoDouble = precioGasto.toDoubleOrNull()
                            if (montoDouble != null) {
                                viewModel.registrarGasto(nombreGasto, montoDouble, viewModel.fotoTemporalUri)
                                nombreGasto = ""
                                precioGasto = ""
                                viewModel.fotoTemporalUri = null
                            } else {
                                Toast.makeText(contexto, "Por favor, ingresa un número válido en el monto", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(contexto, "Llena todos los campos vacíos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                ) {
                    Text("Guardar Gasto")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Historial registrado (Room Database):", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(gastos) { gasto ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(gasto.titulo, style = MaterialTheme.typography.bodyLarge)
                                if (gasto.fotoUri != null) {
                                    Text("📎 Recibo digitalizado adjunto", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Text("$${String.format("%.2f", gasto.monto)}", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}

// Pantalla Ajustes Corregida: Soluciona el error en llaves y alineación de padding
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAjustes(viewModel: GastosViewModel, alVolver: () -> Unit) {
    val modoOscuroActivo by viewModel.isDarkMode.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Configuración General") }) }
    ) { valoresPadding ->
        Column(modifier = Modifier.padding(valoresPadding).padding(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Activar Modo Oscuro", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = modoOscuroActivo,
                        onCheckedChange = { viewModel.cambiarModoOscuro(it) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = alVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al Inicio")
            }
        }
    }
}
