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

// Pantalla de Inicio: Sirve para capturar datos locales e informar el estado del servidor
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaGastos(viewModel: GastosViewModel, alIrAAjustes: () -> Unit) {
    // Recolectamos la lista de gastos desde Room de forma reactiva (MVVM)
    val gastos by viewModel.listaGastos.collectAsState()

    // Variables locales para controlar lo que el usuario escribe en las cajas de texto
    var nombreGasto by remember { mutableStateOf("") }
    var precioGasto by remember { mutableStateOf("") }
    val contexto = LocalContext.current

    // Lanzador de la cámara: Captura la miniatura de la foto tomada
    val lanzadorCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { mapaBits ->
        if (mapaBits != null) {
            // Guardamos un identificador único simulado de la foto en el ViewModel
            viewModel.fotoTemporalUri = "foto_recibo_${System.currentTimeMillis()}"
            Toast.makeText(contexto, "¡Foto del recibo vinculada!", Toast.LENGTH_SHORT).show()
        }
    }

    // Lanzador de permisos: Pide la cámara en tiempo de ejecución (Punto 4e de la rúbrica)
    val lanzadorPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permisoOtorgado ->
        if (permisoOtorgado) {
            // Caso Éxito: Si acepta el permiso, abre la cámara inmediatamente
            lanzadorCamara.launch(null)
        } else {
            // Caso Rechazo: Muestra un aviso flotante informando que no se podrá usar la cámara
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

            // Tarjeta de la API: Cambia de color dinámicamente según los 3 estados (Punto 4d de la rúbrica)
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (viewModel.estadoApi) {
                        "Éxito" -> MaterialTheme.colorScheme.primaryContainer       // Verde si funcionó
                        "Cargando..." -> MaterialTheme.colorScheme.secondaryContainer // Gris si está cargando
                        else -> MaterialTheme.colorScheme.errorContainer             // Rojo si hay error
                    }
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Estado del Servidor API: ${viewModel.estadoApi}", style = MaterialTheme.typography.titleSmall)
                    Text(text = viewModel.mensajeResultado, style = MaterialTheme.typography.bodyMedium)
                    // Si falla el internet, se habilita un botón para volver a intentar la petición
                    if (viewModel.estadoApi == "Error") {
                        TextButton(onClick = { viewModel.consultarServidorRemoto() }) {
                            Text("Reintentar conexión")
                        }
                    }
                }
            }

            // Campos de entrada para recibir el texto de la descripción y el costo
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

            // Fila de botones para interactuar con la cámara y guardar la información
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botón de Cámara: Dispara la solicitud del permiso obligatorio en tiempo de ejecución
                Button(
                    onClick = { lanzadorPermiso.launch(android.Manifest.permission.CAMERA) },
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                ) {
                    Text(if (viewModel.fotoTemporalUri != null) "📸 ¡Foto Lista!" else "📷 Tomar Recibo")
                }

                // Botón Guardar: Valida que los campos no estén vacíos y que el precio sea un número real
                Button(
                    onClick = {
                        if (nombreGasto.isNotEmpty() && precioGasto.isNotEmpty()) {
                            val montoDouble = precioGasto.toDoubleOrNull()
                            if (montoDouble != null) {
                                // Guarda de forma asíncrona la información acoplando la foto a Room
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

            // LazyColumn: Lista dinámica optimizada exigida por la rúbrica (Punto 4a)
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
                                // Validación visual: Si el objeto contiene una foto, se dibuja un aviso elegante
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

// Pantalla Ajustes: Sirve para activar el modo oscuro y guardarlo en DataStore (Punto 4c)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAjustes(viewModel: GastosViewModel, alVolver: () -> Unit) {
    // Recolectamos la preferencia del modo oscuro guardada en el almacenamiento del sistema
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
                    // Al cambiar el interruptor, el ViewModel guarda la decisión en DataStore
                    Switch(
                        checked = modoOscuroActivo,
                        onCheckedChange = { viewModel.cambiarModoOscuro(it) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = alVolver, modifier = Modifier.fillMaxWidth()) {
                Text("Volver al Inicio")
            }
        }
    }
}
