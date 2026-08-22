package com.emely.gastosapp

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import java.io.File

private val categoriasNuevoGasto = listOf(
    "Alimentación",
    "Transporte",
    "Estudios",
    "Salud",
    "Hogar",
    "Entretenimiento",
    "Otros"
)

private val VerdeRegistro = Color(0xFF0B806F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaNuevoGasto(
    viewModel: GastosViewModel,
    alVolver: () -> Unit,
    alGuardar: () -> Unit
) {

    val contexto = LocalContext.current

    var descripcion by remember {
        mutableStateOf("")
    }

    var monto by remember {
        mutableStateOf("")
    }

    var categoria by remember {
        mutableStateOf(
            categoriasNuevoGasto.first()
        )
    }

    var menuExpandido by remember {
        mutableStateOf(false)
    }

    // Recibe la fotografía tomada y la guarda temporalmente.
    val camara =
        rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->

            if (bitmap != null) {

                viewModel.guardarFoto(bitmap)

                Toast.makeText(
                    contexto,
                    "Recibo agregado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // Solicita acceso a la cámara cuando hace falta.
    val permisoCamara =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { concedido ->

            if (concedido) {

                camara.launch(null)

            } else {

                Toast.makeText(
                    contexto,
                    "No se concedió permiso para usar la cámara",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    fun abrirCamara() {

        val permisoDisponible =
            ContextCompat.checkSelfPermission(
                contexto,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        if (permisoDisponible) {

            camara.launch(null)

        } else {

            permisoCamara.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    Scaffold(

        topBar = {

            CenterAlignedTopAppBar(

                title = {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Nuevo gasto",
                            fontWeight =
                                FontWeight.Black
                        )

                        Text(
                            text =
                                "Registra un nuevo movimiento",
                            style =
                                MaterialTheme
                                    .typography
                                    .labelSmall
                        )
                    }
                },

                navigationIcon = {

                    TextButton(
                        onClick = alVolver
                    ) {

                        Text(
                            text = "← Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            contentPadding =
                PaddingValues(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(26.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .primaryContainer
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(18.dp)
                    ) {

                        Text(
                            text = "💸",
                            fontSize = 32.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "Registra tus gastos",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,

                            fontWeight =
                                FontWeight.Black
                        )

                        Text(
                            text =
                                "Completa los datos y guarda el movimiento en tu historial.",

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }
                }
            }

            item {

                OutlinedTextField(
                    value =
                        descripcion,

                    onValueChange = {
                        descripcion = it
                    },

                    label = {
                        Text(
                            text =
                                "Descripción"
                        )
                    },

                    placeholder = {
                        Text(
                            text =
                                "Ej. Almuerzo, gasolina, matrícula..."
                        )
                    },

                    leadingIcon = {
                        Text(
                            text = "✏️"
                        )
                    },

                    singleLine = true,

                    shape =
                        RoundedCornerShape(16.dp),

                    modifier =
                        Modifier.fillMaxWidth()
                )
            }

            item {

                OutlinedTextField(
                    value = monto,

                    onValueChange = { nuevoMonto ->

                        monto =
                            nuevoMonto.filter { caracter ->

                                caracter.isDigit() ||
                                        caracter == '.'
                            }
                    },

                    label = {
                        Text(
                            text = "Monto"
                        )
                    },

                    leadingIcon = {
                        Text(
                            text = "💵"
                        )
                    },

                    prefix = {
                        Text(
                            text = "$ "
                        )
                    },

                    singleLine = true,

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Decimal
                        ),

                    shape =
                        RoundedCornerShape(16.dp),

                    modifier =
                        Modifier.fillMaxWidth()
                )
            }

            item {

                ExposedDropdownMenuBox(
                    expanded =
                        menuExpandido,

                    onExpandedChange = {

                        menuExpandido =
                            !menuExpandido
                    }
                ) {

                    OutlinedTextField(
                        value =
                            categoria,

                        onValueChange = {},

                        readOnly = true,

                        label = {
                            Text(
                                text =
                                    "Categoría"
                            )
                        },

                        leadingIcon = {

                            Text(
                                text =
                                    iconoCategoriaNuevo(
                                        categoria
                                    )
                            )
                        },

                        trailingIcon = {

                            ExposedDropdownMenuDefaults
                                .TrailingIcon(
                                    expanded =
                                        menuExpandido
                                )
                        },

                        modifier =
                            Modifier
                                .menuAnchor()
                                .fillMaxWidth(),

                        shape =
                            RoundedCornerShape(16.dp)
                    )

                    DropdownMenu(
                        expanded =
                            menuExpandido,

                        onDismissRequest = {

                            menuExpandido =
                                false
                        }
                    ) {

                        categoriasNuevoGasto.forEach {
                                opcion ->

                            DropdownMenuItem(

                                text = {

                                    Row(
                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {

                                        Text(
                                            text =
                                                iconoCategoriaNuevo(
                                                    opcion
                                                )
                                        )

                                        Spacer(
                                            modifier =
                                                Modifier.width(
                                                    10.dp
                                                )
                                        )

                                        Text(
                                            text =
                                                opcion
                                        )
                                    }
                                },

                                onClick = {

                                    categoria =
                                        opcion

                                    menuExpandido =
                                        false
                                }
                            )
                        }
                    }
                }
            }

            // Muestra el recibo antes de guardar el gasto.
            if (
                viewModel.fotoTemporalUri != null
            ) {

                item {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(20.dp)
                    ) {

                        Row(
                            modifier =
                                Modifier.padding(14.dp),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            AsyncImage(
                                model =
                                    File(
                                        viewModel
                                            .fotoTemporalUri!!
                                    ),

                                contentDescription =
                                    "Recibo del gasto",

                                contentScale =
                                    ContentScale.Crop,

                                modifier =
                                    Modifier
                                        .size(82.dp)
                                        .clip(
                                            RoundedCornerShape(
                                                16.dp
                                            )
                                        )
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(12.dp)
                            )

                            Column(
                                modifier =
                                    Modifier.weight(1f)
                            ) {

                                Text(
                                    text =
                                        "Recibo listo",

                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    text =
                                        "La fotografía se asociará al gasto guardado.",

                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodySmall
                                )
                            }

                            TextButton(
                                onClick = {

                                    viewModel
                                        .quitarFotoTemporal()
                                }
                            ) {

                                Text(
                                    text =
                                        "Quitar"
                                )
                            }
                        }
                    }
                }
            }

            item {

                OutlinedButton(
                    onClick = {
                        abrirCamara()
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(52.dp),

                    shape =
                        RoundedCornerShape(16.dp)
                ) {

                    Text(
                        text =
                            "📷 Fotografiar recibo"
                    )
                }
            }

            item {

                Button(
                    onClick = {

                        val valor =
                            monto.toDoubleOrNull()

                        when {

                            descripcion.isBlank() -> {

                                Toast.makeText(
                                    contexto,
                                    "Escribe una descripción",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            valor == null ||
                                    valor <= 0 -> {

                                Toast.makeText(
                                    contexto,
                                    "Ingresa un monto válido",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {

                                viewModel.registrarGasto(
                                    descripcion,
                                    valor,
                                    categoria
                                )

                                Toast.makeText(
                                    contexto,
                                    "Gasto registrado correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()

                                alGuardar()
                            }
                        }
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                    shape =
                        RoundedCornerShape(18.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                VerdeRegistro
                        )
                ) {

                    Text(
                        text =
                            "Guardar gasto",

                        color =
                            Color.White,

                        fontWeight =
                            FontWeight.Bold,

                        fontSize =
                            16.sp
                    )
                }
            }

            item {

                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(18.dp),

                    color =
                        MaterialTheme
                            .colorScheme
                            .secondaryContainer
                ) {

                    Text(
                        text =
                            "💡 El gasto se guardará en Room y quedará disponible en el historial y las estadísticas.",

                        modifier =
                            Modifier.padding(14.dp),

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall
                    )
                }
            }
        }
    }
}

// Define el símbolo mostrado en el selector de categorías.
private fun iconoCategoriaNuevo(
    categoria: String
): String {

    return when (
        categoria
    ) {

        "Alimentación" -> "🍽️"
        "Transporte" -> "🚌"
        "Estudios" -> "📚"
        "Salud" -> "🩺"
        "Hogar" -> "🏠"
        "Entretenimiento" -> "🎬"

        else -> "💳"
    }
}