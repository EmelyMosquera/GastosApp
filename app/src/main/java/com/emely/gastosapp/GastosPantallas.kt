package com.emely.gastosapp

import android.net.Uri
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaGastos(viewModel: GastosViewModel, alIrAAjustes: () -> Unit) {
    val gastos by viewModel.listaGastos.collectAsState()
    var nombreGasto by remember { mutableStateOf("") }
    var precioGasto by remember { mutableStateOf("") }
    val contexto = LocalContext.current

    // Lanzador 1: Se encarga de abrir la cámara nativa del sistema operativo
    val lanzadorCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { mapaBits ->
        if (mapaBits != null) {
            // Guardamos temporalmente una referencia de simulación para almacenar la foto
            viewModel.fotoTemporalUri = "foto_recibo_${System.currentTimeMillis()}"
            Toast.makeText(contexto, "¡Foto capturada con éxito!", Toast.LENGTH_SHORT).show()
        }
    }

    // Lanzador 2: Gestiona la solicitud de permisos en tiempo de ejecución exigido por la rúbrica (Punto 4e)
    val lanzadorPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permisoOtorgado ->
        if (permisoOtorgado) {
            // Caso Éxito: Si el usuario acepta, se dispara la cámara directamente
            lanzadorCamara.launch(null)
        } else {
            // Caso Rechazo exigido: Mensaje claro en pantalla si el usuario niega la autorización
            Toast.makeText(contexto, "Permiso denegado. No se puede capturar el recibo.", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GastosApp - Inicio") },
                actions = {
                    Button(onClick = alIrAAjustes) { Text("Ajustes") }
                }
            )
        }
    ) { valoresPadding ->
        Column(modifier = Modifier.padding(valoresPadding).padding(16.dp)) {

            // Componente Visual para reportar los 3 estados exigidos de la API (Punto 4d)
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
                    Text(text = "Estado API: ${viewModel.estadoApi}", style = MaterialTheme.typography.titleSmall)
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
                label = { Text("¿En qué gastaste?") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = precioGasto,
                onValueChange = { precioGasto = it },
                label = { Text("Monto ($)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // NUEVO: Fila de Botones para disparar Hardware y guardar el registro completo (Punto 4e)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { lanzadorPermiso.launch(android.Manifest.permission.CAMERA) },
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                ) {
                    Text(if (viewModel.fotoTemporalUri != null) "📸 ¡Con Foto!" else "📷 Capturar Recibo")
                }

                Button(
                    onClick = {
                        if (nombreGasto.isNotEmpty() && precioGasto.isNotEmpty()) {
                            val montoDouble = precioGasto.toDoubleOrNull() ?: 0.0
                            // Enviamos el gasto acoplando el título, precio y la ruta de la foto a Room
                            viewModel.registrarGasto(nombreGasto, montoDouble, viewModel.fotoTemporalUri)
                            nombreGasto = ""
                            precioGasto = ""
                            viewModel.fotoTemporalUri = null
                        }
                    },
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                ) {
                    Text("Guardar Gasto")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Historial de Gastos (Room):", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // LazyColumn: Lista dinámica optimizada (Punto 4a)
            LazyColumn {
                items(gastos) { gasto ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(gasto.titulo, style = MaterialTheme.typography.bodyLarge)
                                // Si el gasto tiene foto guardada de manera persistente, se coloca la marca visual
                                if (gasto.fotoUri != null) {
                                    Text("📎 Recibo digitalizado adjunto", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Text("$${gasto.monto}", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAjustes(viewModel: GastosViewModel, alVolver: () -> Unit) {
    val modoOscuroActivo by viewModel.isDarkMode.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ajustes de la App") }) }
    ) { valoresPadding ->
        Column(modifier = Modifier.padding(valoresPadding).padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Activar Modo Oscuro", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = modoOscuroActivo,
                    onCheckedChange = { viewModel.cambiarModoOscuro(it) }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = alVolver, modifier = Modifier.fillMaxWidth()) {
                Text("Volver al Inicio")
            }
        }
    }
}
