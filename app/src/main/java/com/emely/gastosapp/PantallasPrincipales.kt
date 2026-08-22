package com.emely.gastosapp

import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private val VerdeDashboard =
    Color(0xFF0B806F)

private val VerdeDashboardOscuro =
    Color(0xFF075E54)

private val TurquesaDashboard =
    Color(0xFF42C2A8)

@Composable
fun PantallaInicio(
    viewModel: GastosViewModel,
    alNuevoGasto: () -> Unit,
    alHistorial: () -> Unit,
    alEstadisticas: () -> Unit,
    alAjustes: () -> Unit
) {

    val gastos by
    viewModel
        .listaGastos
        .collectAsState()

    val total =
        gastos.sumOf {
            it.monto
        }

    val categoriaPrincipal =
        gastos
            .groupBy {
                it.categoria
            }
            .maxByOrNull {
                    entrada ->

                entrada.value.sumOf {
                        gasto ->
                    gasto.monto
                }
            }
            ?.key
            ?: "Sin datos"

    Scaffold(

        containerColor =
            MaterialTheme
                .colorScheme
                .background

    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            contentPadding =
                PaddingValues(
                    18.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(
                    16.dp
                )
        ) {

            item {

                Text(
                    text =
                        "Hola 👋",

                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium,

                    fontWeight =
                        FontWeight.Black
                )

                Text(
                    text =
                        "Revisa cómo van tus gastos.",

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }

            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush =
                                Brush.linearGradient(
                                    listOf(
                                        VerdeDashboardOscuro,
                                        VerdeDashboard,
                                        TurquesaDashboard
                                    )
                                ),

                            shape =
                                RoundedCornerShape(
                                    30.dp
                                )
                        )
                        .padding(
                            22.dp
                        )
                ) {

                    Column {

                        Text(
                            text =
                                "Total registrado",

                            color =
                                Color.White.copy(
                                    alpha = 0.80f
                                )
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    4.dp
                                )
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

                            color =
                                Color.White,

                            fontSize =
                                36.sp,

                            fontWeight =
                                FontWeight.Black
                        )

                        Spacer(
                            modifier =
                                Modifier.height(
                                    18.dp
                                )
                        )

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            Column {

                                Text(
                                    text =
                                        "Movimientos",

                                    color =
                                        Color.White.copy(
                                            alpha =
                                                0.70f
                                        ),

                                    fontSize =
                                        12.sp
                                )

                                Text(
                                    text =
                                        gastos
                                            .size
                                            .toString(),

                                    color =
                                        Color.White,

                                    fontWeight =
                                        FontWeight.Bold,

                                    fontSize =
                                        18.sp
                                )
                            }

                            Column(
                                horizontalAlignment =
                                    Alignment.End
                            ) {

                                Text(
                                    text =
                                        "Categoría principal",

                                    color =
                                        Color.White.copy(
                                            alpha =
                                                0.70f
                                        ),

                                    fontSize =
                                        12.sp
                                )

                                Text(
                                    text =
                                        categoriaPrincipal,

                                    color =
                                        Color.White,

                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {

                Button(
                    onClick =
                        alNuevoGasto,

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(
                                55.dp
                            ),

                    shape =
                        RoundedCornerShape(
                            17.dp
                        ),

                    colors =
                        ButtonDefaults
                            .buttonColors(
                                containerColor =
                                    VerdeDashboard
                            )
                ) {

                    Text(
                        text =
                            "＋ Registrar nuevo gasto",

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            item {

                Text(
                    text =
                        "Accesos rápidos",

                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,

                    fontWeight =
                        FontWeight.Black
                )
            }

            item {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            10.dp
                        )
                ) {

                    AccesoRapido(
                        modifier =
                            Modifier.weight(
                                1f
                            ),

                        icono =
                            "📋",

                        titulo =
                            "Historial",

                        alClick =
                            alHistorial
                    )

                    AccesoRapido(
                        modifier =
                            Modifier.weight(
                                1f
                            ),

                        icono =
                            "📊",

                        titulo =
                            "Estadísticas",

                        alClick =
                            alEstadisticas
                    )

                    AccesoRapido(
                        modifier =
                            Modifier.weight(
                                1f
                            ),

                        icono =
                            "⚙️",

                        titulo =
                            "Ajustes",

                        alClick =
                            alAjustes
                    )
                }
            }

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            22.dp
                        )
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    16.dp
                                ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Surface(
                            modifier =
                                Modifier.size(
                                    46.dp
                                ),

                            shape =
                                CircleShape,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .secondaryContainer
                        ) {

                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Text(
                                    text = "🌐",
                                    fontSize = 20.sp
                                )
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.width(
                                    12.dp
                                )
                        )

                        Column(
                            modifier =
                                Modifier.weight(
                                    1f
                                )
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
                                        .bodySmall
                            )
                        }
                    }
                }
            }

            item {

                Text(
                    text =
                        "Últimos movimientos",

                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,

                    fontWeight =
                        FontWeight.Black
                )
            }

            if (
                gastos.isEmpty()
            ) {

                item {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(
                                22.dp
                            )
                    ) {

                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        24.dp
                                    ),

                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Text(
                                text =
                                    "💳",

                                fontSize =
                                    36.sp
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        8.dp
                                    )
                            )

                            Text(
                                text =
                                    "Sin movimientos todavía",

                                fontWeight =
                                    FontWeight.Bold
                            )

                            Text(
                                text =
                                    "Registra tu primer gasto para comenzar."
                            )
                        }
                    }
                }

            } else {

                items(
                    items =
                        gastos.take(3),

                    key = {
                        it.id
                    }
                ) { gasto ->

                    ResumenGasto(
                        gasto =
                            gasto
                    )
                }
            }
        }
    }
}

@Composable
private fun AccesoRapido(
    modifier: Modifier,
    icono: String,
    titulo: String,
    alClick: () -> Unit
) {

    Card(
        onClick =
            alClick,

        modifier =
            modifier,

        shape =
            RoundedCornerShape(
                20.dp
            ),

        elevation =
            CardDefaults
                .cardElevation(
                    defaultElevation =
                        2.dp
                )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        14.dp
                    ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    icono,

                fontSize =
                    27.sp
            )

            Spacer(
                modifier =
                    Modifier.height(
                        7.dp
                    )
            )

            Text(
                text =
                    titulo,

                fontWeight =
                    FontWeight.Bold,

                fontSize =
                    12.sp
            )
        }
    }
}

@Composable
private fun ResumenGasto(
    gasto: Gasto
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                18.dp
            )
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        14.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text =
                    iconoCategoria(
                        gasto.categoria
                    ),

                fontSize =
                    26.sp
            )

            Spacer(
                modifier =
                    Modifier.width(
                        12.dp
                    )
            )

            Column(
                modifier =
                    Modifier.weight(
                        1f
                    )
            ) {

                Text(
                    text =
                        gasto.titulo,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        "${gasto.categoria} · ${gasto.fecha}",

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall
                )
            }

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
                    FontWeight.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorial(
    viewModel: GastosViewModel,
    alVolver: () -> Unit
) {

    val gastos by
    viewModel
        .listaGastos
        .collectAsState()

    val contexto =
        LocalContext.current

    Scaffold(

        topBar = {

            CenterAlignedTopAppBar(

                title = {
                    Text(
                        text =
                            "Historial"
                    )
                },

                navigationIcon = {

                    TextButton(
                        onClick =
                            alVolver
                    ) {

                        Text(
                            text =
                                "←"
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
                    16.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(
                    10.dp
                )
        ) {

            if (
                gastos.isEmpty()
            ) {

                item {

                    Text(
                        text =
                            "Todavía no existen movimientos registrados."
                    )
                }

            } else {

                items(
                    items =
                        gastos,

                    key = {
                        it.id
                    }
                ) { gasto ->

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(
                                18.dp
                            )
                    ) {

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        14.dp
                                    ),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(
                                text =
                                    iconoCategoria(
                                        gasto.categoria
                                    ),

                                fontSize =
                                    26.sp
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(
                                        12.dp
                                    )
                            )

                            Column(
                                modifier =
                                    Modifier.weight(
                                        1f
                                    )
                            ) {

                                Text(
                                    text =
                                        gasto.titulo,

                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    text =
                                        gasto.categoria,

                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodySmall
                                )

                                Text(
                                    text =
                                        gasto.fecha,

                                    style =
                                        MaterialTheme
                                            .typography
                                            .labelSmall
                                )
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
                                        FontWeight.Black
                                )

                                TextButton(
                                    onClick = {

                                        viewModel
                                            .eliminarGasto(
                                                gasto
                                            )

                                        Toast.makeText(
                                            contexto,
                                            "Movimiento eliminado",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                ) {

                                    Text(
                                        text =
                                            "Eliminar",

                                        color =
                                            MaterialTheme
                                                .colorScheme
                                                .error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEstadisticas(
    viewModel: GastosViewModel,
    alVolver: () -> Unit
) {

    val gastos by
    viewModel
        .listaGastos
        .collectAsState()

    val total =
        gastos.sumOf {
            it.monto
        }

    val promedio =
        if (
            gastos.isNotEmpty()
        ) {

            total /
                    gastos.size

        } else {

            0.0
        }

    val agrupados =
        gastos
            .groupBy {
                it.categoria
            }
            .mapValues {
                    entrada ->

                entrada
                    .value
                    .sumOf {
                            gasto ->
                        gasto.monto
                    }
            }
            .toList()
            .sortedByDescending {
                it.second
            }

    Scaffold(

        topBar = {

            CenterAlignedTopAppBar(

                title = {
                    Text(
                        text =
                            "Estadísticas"
                    )
                },

                navigationIcon = {

                    TextButton(
                        onClick =
                            alVolver
                    ) {

                        Text(
                            text =
                                "←"
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
                    16.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(
                    14.dp
                )
        ) {

            item {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            10.dp
                        )
                ) {

                    TarjetaDato(
                        modifier =
                            Modifier.weight(
                                1f
                            ),

                        titulo =
                            "Total",

                        valor =
                            "$${
                                String.format(
                                    Locale.US,
                                    "%.2f",
                                    total
                                )
                            }"
                    )

                    TarjetaDato(
                        modifier =
                            Modifier.weight(
                                1f
                            ),

                        titulo =
                            "Promedio",

                        valor =
                            "$${
                                String.format(
                                    Locale.US,
                                    "%.2f",
                                    promedio
                                )
                            }"
                    )
                }
            }

            item {

                Text(
                    text =
                        "Gastos por categoría",

                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,

                    fontWeight =
                        FontWeight.Black
                )
            }

            if (
                agrupados.isEmpty()
            ) {

                item {

                    Text(
                        text =
                            "Registra movimientos para visualizar las estadísticas."
                    )
                }

            } else {

                items(
                    items =
                        agrupados
                ) { entrada ->

                    val categoria =
                        entrada.first

                    val valor =
                        entrada.second

                    val progreso =
                        if (
                            total > 0
                        ) {

                            (
                                    valor /
                                            total
                                    )
                                .toFloat()

                        } else {

                            0f
                        }

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(
                                18.dp
                            )
                    ) {

                        Column(
                            modifier =
                                Modifier.padding(
                                    15.dp
                                )
                        ) {

                            Row(
                                modifier =
                                    Modifier.fillMaxWidth(),

                                horizontalArrangement =
                                    Arrangement.SpaceBetween
                            ) {

                                Text(
                                    text =
                                        "${
                                            iconoCategoria(
                                                categoria
                                            )
                                        } $categoria",

                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(
                                    text =
                                        "$${
                                            String.format(
                                                Locale.US,
                                                "%.2f",
                                                valor
                                            )
                                        }"
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        9.dp
                                    )
                            )

                            LinearProgressIndicator(
                                progress = {
                                    progreso
                                },

                                modifier =
                                    Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaDato(
    modifier: Modifier,
    titulo: String,
    valor: String
) {

    Card(
        modifier =
            modifier,

        shape =
            RoundedCornerShape(
                20.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    16.dp
                )
        ) {

            Text(
                text =
                    titulo,

                style =
                    MaterialTheme
                        .typography
                        .bodySmall
            )

            Spacer(
                modifier =
                    Modifier.height(
                        5.dp
                    )
            )

            Text(
                text =
                    valor,

                fontWeight =
                    FontWeight.Black,

                fontSize =
                    21.sp
            )
        }
    }
}

private fun iconoCategoria(
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