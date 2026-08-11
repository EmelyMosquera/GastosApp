package com.emely.gastosapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emely.gastosapp.ui.theme.GastosAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Inicialización del ViewModel que contiene los datos de Room y DataStore
            val miViewModel: GastosViewModel = viewModel()

            // Recolectamos el estado del Modo Oscuro en tiempo real
            val modoOscuro by miViewModel.isDarkMode.collectAsState()

            // Aplicamos el tema gráfico dinámico de la aplicación
            GastosAppTheme(darkTheme = modoOscuro) {
                val navegador = rememberNavController()

                // NavHost: Define la estructura de navegación exigida en la rúbrica
                NavHost(navController = navegador, startDestination = "pantalla_inicio") {
                    composable("pantalla_inicio") {
                        PantallaListaGastos(
                            viewModel = miViewModel,
                            alIrAAjustes = { navegador.navigate("pantalla_config") }
                        )
                    }
                    composable("pantalla_config") {
                        PantallaAjustes(
                            viewModel = miViewModel,
                            alVolver = { navegador.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
