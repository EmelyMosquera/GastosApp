package com.emely.gastosapp

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import java.io.File
import java.util.Locale

// Categorías disponibles para organizar los movimientos registrados.
private val categorias = listOf(
    "Alimentación",
    "Transporte",
    "Estudios",
    "Salud",
    "Hogar",
    "Entretenimiento",
    "Otros"
)

// Tonos principales utilizados en la identidad visual de GastosApp.
private val VerdeOscuro = Color(0xFF075E54)
private val VerdePrincipal = Color(0xFF0B806F)
private val VerdeClaro = Color(0xFFBFEBDD)
private val Turquesa = Color(0xFF42C2A8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaGastos(
    viewModel: GastosViewModel,
    alIrAAjustes: () -> Unit
) {

    // La pantalla permanece pendiente de los cambios almacenados en Room.
    val gastos by viewModel.listaGastos.collectAsState()

    val contexto = LocalContext.current

    // Suma de todos los movimientos registrados hasta el momento.
    val total = gastos.sumOf { gasto ->
        gasto.monto
    }

    // Identifica qué categoría concentra el valor más alto.
    val categoriaMayor =
        gastos
            .groupBy { gasto ->
                gasto.categoria
            }
            .maxByOrNull { entrada ->
                entrada.value.sumOf { gasto ->
                    gasto.monto
                }
            }
            ?.key
            ?: "Sin movimientos"

    var titulo by remember {
        mutableStateOf("")
    }

    var monto by remember {
        mutableStateOf("")
    }

    var categoria by remember {
        mutableStateOf(categorias.first())
    }

    var menuCategorias by remember {
        mutableStateOf(false)
    }

    // Recibe la fotografía tomada con la cámara y la envía al ViewModel.
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

    // Solicita acceso a la cámara solamente cuando todavía no existe permiso.
    val permisoCamara =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { concedido ->

            if (concedido) {

                camara.launch(null)

            } else {

                Toast.makeText(
                    contexto,
                    "Se necesita permiso para utilizar la cámara",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    fun abrirCamara() {

        val permisoConcedido =
            ContextCompat.checkSelfPermission(
                contexto,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        if (permisoConcedido) {

            camara.launch(null)

        } else {

            permisoCamara.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    Scaffold(

        containerColor =
            MaterialTheme.colorScheme.background,

        topBar = {

            CenterAlignedTopAppBar(

                navigationIcon = {

                    Surface(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(42.dp),

                        shape = CircleShape,
                        color = VerdePrincipal
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text = "$",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 21.sp
                            )
                        }
                    }
                },

                title = {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "GastosApp",
                            fontWeight =
                                FontWeight.Black
                        )

                        Text(
                            text =
                                "Organiza tus gastos fácilmente",

                            style =
                                MaterialTheme.typography.labelSmall,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }
                },

                actions = {

                    TextButton(
                        onClick = alIrAAjustes
                    ) {

                        Text(
                            text = "⚙",
                            fontSize = 21.sp
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
                PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 10.dp,
                    bottom = 30.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            // Panel superior con un resumen general del movimiento financiero.
            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(
                                30.dp
                            )
                        )
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    VerdeOscuro,
                                    VerdePrincipal,
                                    Turquesa
                                )
                            )
                        )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp)
                    ) {

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.SpaceBetween,

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Column {

                                Text(
                                    text =
                                        "Total registrado",

                                    color =
                                        Color.White.copy(
                                            alpha = 0.80f
                                        ),

                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyMedium
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(4.dp)
                                )

                                Text(
                                    text =
                                        "$${
                                            String.format(
                                                Locale.US,
                                                "%.2f",
                                                total
                                            )
                                        }",

                                    color = Color.White,
                                    fontSize = 36.sp,
                                    fontWeight =
                                        FontWeight.Black
                                )
                            }

                            Surface(
                                shape = CircleShape,

                                color =
                                    Color.White.copy(
                                        alpha = 0.18f
                                    ),

                                modifier =
                                    Modifier.size(58.dp)
                            ) {

                                Box(
                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Text(
                                        text = "💰",
                                        fontSize = 27.sp
                                    )
                                }
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(18.dp)
                        )

                        HorizontalDivider(
                            color =
                                Color.White.copy(
                                    alpha = 0.20f
                                )
                        )

                        Spacer(
                            modifier =
                                Modifier.height(14.dp)
                        )

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            Column {

                                Text(
                                    text = "Movimientos",

                                    color =
                                        Color.White.copy(
                                            alpha = 0.75f
                                        ),

                                    fontSize = 12.sp
                                )

                                Text(
                                    text =
                                        gastos.size.toString(),

                                    color = Color.White,

                                    fontWeight =
                                        FontWeight.Bold,

                                    fontSize = 18.sp
                                )
                            }

                            Column(
                                horizontalAlignment =
                                    Alignment.End
                            ) {

                                Text(
                                    text =
                                        "Mayor categoría",

                                    color =
                                        Color.White.copy(
                                            alpha = 0.75f
                                        ),

                                    fontSize = 12.sp
                                )

                                Text(
                                    text =
                                        categoriaMayor,

                                    color = Color.White,

                                    fontWeight =
                                        FontWeight.Bold,

                                    fontSize = 15.sp
                                )
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(15.dp)
                        )

                        Surface(
                            color =
                                Color.White.copy(
                                    alpha = 0.14f
                                ),

                            shape =
                                RoundedCornerShape(16.dp)
                        ) {

                            Text(
                                text =
                                    "✨ Registrar los movimientos ayuda a reconocer en qué se utiliza el dinero.",

                                color = Color.White,

                                modifier =
                                    Modifier.padding(12.dp),

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall
                            )
                        }
                    }
                }
            }

            // Muestra información proveniente del servicio remoto.
            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(22.dp),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Surface(
                            shape = CircleShape,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .secondaryContainer,

                            modifier =
                                Modifier.size(46.dp)
                        ) {

                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Text(
                                    text = "🌐",
                                    fontSize = 21.sp
                                )
                            }
                        }

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
                                    "Tipo de cambio",

                                fontWeight =
                                    FontWeight.Bold
                            )

                            Text(
                                text =
                                    viewModel
                                        .mensajeResultado,

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall,

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant
                            )
                        }

                        AssistChip(
                            onClick = {

                                viewModel
                                    .consultarServidorRemoto()
                            },

                            label = {

                                Text(
                                    text =
                                        viewModel.estadoApi
                                )
                            }
                        )
                    }
                }
            }

            // Presenta el formulario como una sección independiente del historial.
            item {

                Column {

                    Text(
                        text =
                            "Registrar gasto",

                        style =
                            MaterialTheme
                                .typography
                                .headlineSmall,

                        fontWeight =
                            FontWeight.Black
                    )

                    Text(
                        text =
                            "Añade los datos principales de tu nuevo movimiento.",

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
            }

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(26.dp),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(18.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        OutlinedTextField(
                            value = titulo,

                            onValueChange = {
                                titulo = it
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
                                        "Ej. Almuerzo, gasolina..."
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

                        ExposedDropdownMenuBox(
                            expanded =
                                menuCategorias,

                            onExpandedChange = {

                                menuCategorias =
                                    !menuCategorias
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
                                            emojiCategoria(
                                                categoria
                                            )
                                    )
                                },

                                trailingIcon = {

                                    ExposedDropdownMenuDefaults
                                        .TrailingIcon(
                                            expanded =
                                                menuCategorias
                                        )
                                },

                                shape =
                                    RoundedCornerShape(16.dp),

                                modifier =
                                    Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                            )

                            DropdownMenu(
                                expanded =
                                    menuCategorias,

                                onDismissRequest = {

                                    menuCategorias =
                                        false
                                }
                            ) {

                                categorias.forEach { opcion ->

                                    DropdownMenuItem(

                                        text = {

                                            Row(
                                                verticalAlignment =
                                                    Alignment.CenterVertically
                                            ) {

                                                Text(
                                                    text =
                                                        emojiCategoria(
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

                                            menuCategorias =
                                                false
                                        }
                                    )
                                }
                            }
                        }

                        // Si existe una fotografía temporal se presenta antes de guardar.
                        if (
                            viewModel
                                .fotoTemporalUri != null
                        ) {

                            Surface(
                                shape =
                                    RoundedCornerShape(18.dp),

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .secondaryContainer
                            ) {

                                Row(
                                    modifier =
                                        Modifier.padding(12.dp),

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
                                            "Fotografía del recibo",

                                        modifier =
                                            Modifier
                                                .size(72.dp)
                                                .clip(
                                                    RoundedCornerShape(
                                                        14.dp
                                                    )
                                                ),

                                        contentScale =
                                            ContentScale.Crop
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
                                                "Recibo agregado",

                                            fontWeight =
                                                FontWeight.Bold
                                        )

                                        Text(
                                            text =
                                                "La fotografía quedará asociada al movimiento.",

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

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {

                            OutlinedButton(
                                onClick = {
                                    abrirCamara()
                                },

                                modifier =
                                    Modifier.weight(1f),

                                shape =
                                    RoundedCornerShape(16.dp)
                            ) {

                                Text(
                                    text =
                                        "📷 Recibo"
                                )
                            }

                            Button(
                                onClick = {

                                    val valor =
                                        monto
                                            .toDoubleOrNull()

                                    when {

                                        titulo.isBlank() -> {

                                            Toast.makeText(
                                                contexto,
                                                "Ingresa una descripción",
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

                                            viewModel
                                                .registrarGasto(
                                                    titulo,
                                                    valor,
                                                    categoria
                                                )

                                            titulo = ""
                                            monto = ""

                                            categoria =
                                                categorias.first()

                                            Toast.makeText(
                                                contexto,
                                                "Gasto guardado correctamente",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                },

                                modifier =
                                    Modifier.weight(1f),

                                shape =
                                    RoundedCornerShape(16.dp),

                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor =
                                            VerdePrincipal
                                    )
                            ) {

                                Text(
                                    text = "Guardar",
                                    color = Color.White,
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Separa visualmente la información histórica del formulario.
            item {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text =
                                "Movimientos recientes",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,

                            fontWeight =
                                FontWeight.Black
                        )

                        Text(
                            text =
                                "Consulta los gastos registrados anteriormente.",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }

                    Surface(
                        shape =
                            CircleShape,

                        color =
                            MaterialTheme
                                .colorScheme
                                .primaryContainer
                    ) {

                        Text(
                            text =
                                gastos.size.toString(),

                            modifier =
                                Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 6.dp
                                ),

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }

            if (
                gastos.isEmpty()
            ) {

                item {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(26.dp)
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),

                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Surface(
                                shape = CircleShape,
                                color = VerdeClaro,

                                modifier =
                                    Modifier.size(72.dp)
                            ) {

                                Box(
                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Text(
                                        text = "💳",
                                        fontSize = 32.sp
                                    )
                                }
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(14.dp)
                            )

                            Text(
                                text =
                                    "Todavía no hay gastos",

                                fontWeight =
                                    FontWeight.Bold,

                                fontSize = 17.sp
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(
                                text =
                                    "Registra el primer movimiento para comenzar a visualizar el historial.",

                                textAlign =
                                    TextAlign.Center,

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant
                            )
                        }
                    }
                }

            } else {

                items(
                    items = gastos,

                    key = { gasto ->
                        gasto.id
                    }
                ) { gasto ->

                    GastoCard(
                        gasto = gasto,

                        alEliminar = {

                            viewModel
                                .eliminarGasto(
                                    gasto
                                )

                            Toast.makeText(
                                contexto,
                                "Gasto eliminado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }
    }
}

// Representa visualmente un movimiento dentro del historial.
@Composable
private fun GastoCard(
    gasto: Gasto,
    alEliminar: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(22.dp),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Row(
            modifier =
                Modifier.padding(15.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Surface(
                shape = CircleShape,

                color =
                    colorCategoria(
                        gasto.categoria
                    ),

                modifier =
                    Modifier.size(52.dp)
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            emojiCategoria(
                                gasto.categoria
                            ),

                        fontSize =
                            23.sp
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.width(13.dp)
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        gasto.titulo,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize = 16.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

                Text(
                    text =
                        "${gasto.categoria} • ${gasto.fecha}",

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )

                if (
                    gasto.fotoUri != null
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            "📎 Recibo adjunto",

                        style =
                            MaterialTheme
                                .typography
                                .labelSmall,

                        color =
                            VerdePrincipal
                    )
                }
            }

            Column(
                horizontalAlignment =
                    Alignment.End
            ) {

                Text(
                    text =
                        "$${
                            String.format(
                                Locale.US,
                                "%.2f",
                                gasto.monto
                            )
                        }",

                    fontWeight =
                        FontWeight.Black,

                    fontSize =
                        17.sp
                )

                TextButton(
                    onClick =
                        alEliminar
                ) {

                    Text(
                        text =
                            "Eliminar",

                        color =
                            MaterialTheme
                                .colorScheme
                                .error,

                        fontSize =
                            12.sp
                    )
                }
            }
        }
    }
}

// Selecciona un símbolo relacionado con la categoría del movimiento.
private fun emojiCategoria(
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

// Proporciona un color identificativo a cada tipo de gasto.
private fun colorCategoria(
    categoria: String
): Color {

    return when (
        categoria
    ) {

        "Alimentación" ->
            Color(0xFFFFE3B3)

        "Transporte" ->
            Color(0xFFCDE7FF)

        "Estudios" ->
            Color(0xFFD9D2FF)

        "Salud" ->
            Color(0xFFFFD5DB)

        "Hogar" ->
            Color(0xFFD4EED8)

        "Entretenimiento" ->
            Color(0xFFFFD8F1)

        else ->
            Color(0xFFE5E7EB)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAjustes(
    viewModel: GastosViewModel,
    alVolver: () -> Unit
) {

    val modoOscuro by
    viewModel
        .isDarkMode
        .collectAsState()

    Scaffold(

        containerColor =
            MaterialTheme
                .colorScheme
                .background,

        topBar = {

            CenterAlignedTopAppBar(

                title = {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text =
                                "Configuración",

                            fontWeight =
                                FontWeight.Black
                        )

                        Text(
                            text =
                                "Personaliza GastosApp",

                            style =
                                MaterialTheme
                                    .typography
                                    .labelSmall
                        )
                    }
                },

                navigationIcon = {

                    TextButton(
                        onClick =
                            alVolver
                    ) {

                        Text(
                            text =
                                "← Volver"
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
                Arrangement.spacedBy(14.dp)
        ) {

            // Encabezado visual para diferenciar la sección de configuración.
            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(
                                26.dp
                            )
                        )
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    VerdeOscuro,
                                    VerdePrincipal
                                )
                            )
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "💰",
                            fontSize = 32.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "Tu dinero, tus decisiones",

                            color = Color.White,

                            fontWeight =
                                FontWeight.Black,

                            fontSize = 20.sp
                        )

                        Text(
                            text =
                                "Configura la aplicación según tus preferencias.",

                            color =
                                Color.White.copy(
                                    alpha = 0.80f
                                )
                        )
                    }
                }
            }

            item {

                Text(
                    text =
                        "Preferencias",

                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,

                    fontWeight =
                        FontWeight.Black
                )
            }

            // Control para guardar la selección de apariencia mediante DataStore.
            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(22.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),

                        verticalAlignment =
                            Alignment.CenterVertically,

                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Row(
                            modifier =
                                Modifier.weight(1f),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Surface(
                                shape = CircleShape,

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .primaryContainer,

                                modifier =
                                    Modifier.size(46.dp)
                            ) {

                                Box(
                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Text(
                                        text =
                                            if (modoOscuro) {
                                                "🌙"
                                            } else {
                                                "☀️"
                                            },

                                        fontSize = 21.sp
                                    )
                                }
                            }

                            Spacer(
                                modifier =
                                    Modifier.width(12.dp)
                            )

                            Column {

                                Text(
                                    text =
                                        "Modo oscuro",

                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    text =
                                        "Esta preferencia se mantiene guardada con DataStore.",

                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodySmall,

                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked =
                                modoOscuro,

                            onCheckedChange =
                                viewModel::cambiarModoOscuro
                        )
                    }
                }
            }

            // Información breve sobre las herramientas utilizadas en el proyecto.
            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(22.dp)
                ) {

                    Column(
                        modifier =
                            Modifier.padding(18.dp)
                    ) {

                        Text(
                            text =
                                "📊 Acerca de GastosApp",

                            fontWeight =
                                FontWeight.Bold,

                            fontSize = 17.sp
                        )

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                "Aplicación móvil creada para registrar, organizar y consultar gastos personales de una manera sencilla."
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        HorizontalDivider()

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        Text(
                            text =
                                "Jetpack Compose • Room • Retrofit • DataStore",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,

                            color =
                                VerdePrincipal,

                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }
            }

            // Recordatorio de que la información principal queda almacenada localmente.
            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(22.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .secondaryContainer
                        )
                ) {

                    Column(
                        modifier =
                            Modifier.padding(18.dp)
                    ) {

                        Text(
                            text =
                                "🔐 Información local",

                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(5.dp)
                        )

                        Text(
                            text =
                                "Los gastos registrados y los recibos permanecen almacenados localmente en el dispositivo.",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium
                        )
                    }
                }
            }
        }
    }
}