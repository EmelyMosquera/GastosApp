package com.emely.gastosapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VerdeInicio = Color(0xFF075E54)
private val VerdeMedio = Color(0xFF0B806F)
private val TurquesaInicio = Color(0xFF3CC7AA)

@Composable
fun PantallaBienvenida(
    alComenzar: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        VerdeInicio,
                        VerdeMedio,
                        TurquesaInicio
                    )
                )
            )
            .padding(24.dp)
    ) {

        Column(
            modifier =
                Modifier.fillMaxSize(),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            // Elemento principal de identidad visual.
            Surface(
                modifier =
                    Modifier.size(120.dp),

                shape =
                    CircleShape,

                color =
                    Color.White.copy(
                        alpha = 0.16f
                    )
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = "💰",
                        fontSize = 58.sp
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )

            Text(
                text =
                    "GastosApp",

                color =
                    Color.White,

                fontWeight =
                    FontWeight.Black,

                fontSize =
                    38.sp
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "Tus gastos bajo control",

                color =
                    Color.White.copy(
                        alpha = 0.92f
                    ),

                fontSize =
                    20.sp,

                fontWeight =
                    FontWeight.SemiBold
            )

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            Text(
                text =
                    "Registra tus movimientos, organiza tus categorías y comprende mejor en qué utilizas tu dinero.",

                color =
                    Color.White.copy(
                        alpha = 0.82f
                    ),

                textAlign =
                    TextAlign.Center,

                style =
                    MaterialTheme
                        .typography
                        .bodyLarge,

                modifier =
                    Modifier.padding(
                        horizontal =
                            14.dp
                    )
            )

            Spacer(
                modifier =
                    Modifier.height(34.dp)
            )

            Button(
                onClick =
                    alComenzar,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),

                shape =
                    RoundedCornerShape(
                        18.dp
                    ),

                colors =
                    ButtonDefaults
                        .buttonColors(
                            containerColor =
                                Color.White
                        )
            ) {

                Text(
                    text =
                        "Comenzar ahora",

                    color =
                        VerdeInicio,

                    fontSize =
                        17.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                modifier =
                    Modifier.height(30.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceEvenly
            ) {

                MiniCaracteristica(
                    icono = "💾",
                    texto = "Room"
                )

                MiniCaracteristica(
                    icono = "🌐",
                    texto = "Retrofit"
                )

                MiniCaracteristica(
                    icono = "📷",
                    texto = "Cámara"
                )

                MiniCaracteristica(
                    icono = "⚙️",
                    texto = "DataStore"
                )
            }
        }
    }
}

@Composable
private fun MiniCaracteristica(
    icono: String,
    texto: String
) {

    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Box(
            modifier =
                Modifier
                    .size(44.dp)
                    .clip(
                        CircleShape
                    )
                    .background(
                        Color.White.copy(
                            alpha = 0.14f
                        )
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    icono,

                fontSize =
                    20.sp
            )
        }

        Spacer(
            modifier =
                Modifier.height(5.dp)
        )

        Text(
            text =
                texto,

            color =
                Color.White.copy(
                    alpha = 0.82f
                ),

            fontSize =
                11.sp
        )
    }
}