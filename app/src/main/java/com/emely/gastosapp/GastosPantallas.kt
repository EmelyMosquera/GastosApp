package com.emely.gastosapp

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import java.io.File
import java.util.Locale

private val categorias = listOf("Alimentación", "Transporte", "Estudios", "Salud", "Hogar", "Entretenimiento", "Otros")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaGastos(viewModel: GastosViewModel, alIrAAjustes: () -> Unit) {
    val gastos by viewModel.listaGastos.collectAsState()
    val contexto = LocalContext.current
    val total = gastos.sumOf { it.monto }

    var titulo by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(categorias.first()) }
    var menuCategorias by remember { mutableStateOf(false) }

    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            viewModel.guardarFoto(bitmap)
            Toast.makeText(contexto, "Recibo agregado", Toast.LENGTH_SHORT).show()
        }
    }

    val permisoCamara = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) camara.launch(null)
        else Toast.makeText(contexto, "Se necesita permiso para usar la cámara", Toast.LENGTH_SHORT).show()
    }

    fun abrirCamara() {
        if (ContextCompat.checkSelfPermission(contexto, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            camara.launch(null)
        } else {
            permisoCamara.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("GastosApp", fontWeight = FontWeight.Bold)
                        Text("Control personal de gastos", style = MaterialTheme.typography.labelMedium)
                    }
                },
                actions = { TextButton(onClick = alIrAAjustes) { Text("⚙ Ajustes") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Resumen", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "$${String.format(Locale.US, "%.2f", total)}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text("${gastos.size} movimientos registrados", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Tipo de cambio", fontWeight = FontWeight.SemiBold)
                                Text(viewModel.mensajeResultado, style = MaterialTheme.typography.bodySmall)
                            }
                            AssistChip(
                                onClick = { viewModel.consultarServidorRemoto() },
                                label = { Text(viewModel.estadoApi) }
                            )
                        }
                    }
                }
            }

            item { Text("Registrar gasto", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }

            item {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Descripción") },
                    placeholder = { Text("Ej. Almuerzo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = monto,
                    onValueChange = { nuevo -> monto = nuevo.filter { it.isDigit() || it == '.' } },
                    label = { Text("Monto") },
                    prefix = { Text("$ ") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                ExposedDropdownMenuBox(expanded = menuCategorias, onExpandedChange = { menuCategorias = !menuCategorias }) {
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuCategorias) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = menuCategorias, onDismissRequest = { menuCategorias = false }) {
                        categorias.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = { categoria = opcion; menuCategorias = false }
                            )
                        }
                    }
                }
            }

            item {
                if (viewModel.fotoTemporalUri != null) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = File(viewModel.fotoTemporalUri!!),
                                contentDescription = "Recibo",
                                modifier = Modifier.size(72.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Recibo listo", fontWeight = FontWeight.SemiBold)
                                Text("La fotografía se guardará con el gasto", style = MaterialTheme.typography.bodySmall)
                            }
                            TextButton(onClick = { viewModel.quitarFotoTemporal() }) { Text("Quitar") }
                        }
                    }
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = { abrirCamara() }, modifier = Modifier.weight(1f)) {
                        Text("📷 Recibo")
                    }
                    Button(
                        onClick = {
                            val valor = monto.toDoubleOrNull()
                            when {
                                titulo.isBlank() -> Toast.makeText(contexto, "Escribe una descripción", Toast.LENGTH_SHORT).show()
                                valor == null || valor <= 0 -> Toast.makeText(contexto, "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
                                else -> {
                                    viewModel.registrarGasto(titulo, valor, categoria)
                                    titulo = ""
                                    monto = ""
                                    categoria = categorias.first()
                                    Toast.makeText(contexto, "Gasto guardado", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Guardar") }
                }
            }

            item {
                Spacer(Modifier.height(4.dp))
                Text("Historial", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            if (gastos.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💳", style = MaterialTheme.typography.displaySmall)
                            Spacer(Modifier.height(8.dp))
                            Text("Todavía no hay gastos", fontWeight = FontWeight.SemiBold)
                            Text("Registra el primero usando el formulario superior", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            } else {
                items(gastos, key = { it.id }) { gasto ->
                    GastoCard(gasto = gasto, alEliminar = {
                        viewModel.eliminarGasto(gasto)
                        Toast.makeText(contexto, "Gasto eliminado", Toast.LENGTH_SHORT).show()
                    })
                }
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun GastoCard(gasto: Gasto, alEliminar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(emojiCategoria(gasto.categoria), style = MaterialTheme.typography.titleLarge)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(gasto.titulo, fontWeight = FontWeight.SemiBold)
                Text("${gasto.categoria} · ${gasto.fecha}", style = MaterialTheme.typography.bodySmall)
                if (gasto.fotoUri != null) Text("📎 Recibo adjunto", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$${String.format(Locale.US, "%.2f", gasto.monto)}", fontWeight = FontWeight.Bold)
                TextButton(onClick = alEliminar) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

private fun emojiCategoria(categoria: String): String = when (categoria) {
    "Alimentación" -> "🍽️"
    "Transporte" -> "🚌"
    "Estudios" -> "📚"
    "Salud" -> "🩺"
    "Hogar" -> "🏠"
    "Entretenimiento" -> "🎬"
    else -> "💳"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAjustes(viewModel: GastosViewModel, alVolver: () -> Unit) {
    val modoOscuro by viewModel.isDarkMode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = { TextButton(onClick = alVolver) { Text("← Volver") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                Row(
                    Modifier.fillMaxWidth().padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Modo oscuro", fontWeight = FontWeight.SemiBold)
                        Text("Guarda esta preferencia con DataStore", style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(checked = modoOscuro, onCheckedChange = viewModel::cambiarModoOscuro)
                }
            }

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text("Acerca de GastosApp", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text("Aplicación para registrar y consultar gastos personales de forma sencilla.")
                    Spacer(Modifier.height(6.dp))
                    Text("Room · DataStore · Retrofit · Jetpack Compose", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
