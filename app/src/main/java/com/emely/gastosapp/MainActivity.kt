package com.emely.gastosapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emely.gastosapp.ui.theme.GastosAppTheme

// Actividad Principal: Punto de entrada nativo de Android que inicializa la interfaz Compose
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilita el diseño de borde a borde para aprovechar toda la pantalla
        enableEdgeToEdge()

        // CORRECCIÓN CRÍTICA: Inicialización explícita y segura compatible con el SDK 35 para evitar cierres forzados
        val miViewModel = ViewModelProvider(this)[GastosViewModel::class.java]

        setContent {
            // Recolectamos el estado del Modo Oscuro guardado en DataStore en tiempo real
            val modoOscuro by miViewModel.isDarkMode.collectAsState()

            // Aplicamos el contenedor de diseño personalizado reactivo al tema elegido
            GastosAppTheme(darkTheme = modoOscuro) {
                // Creamos el controlador central para gestionar los viajes entre pantallas
                val navegador = rememberNavController()

                // NavHost: Define las rutas de navegación requeridas en el punto 4a de la rúbrica
                NavHost(navController = navegador, startDestination = "pantalla_inicio") {
                    // Ruta 1: Pantalla principal con el historial de gastos locales y estado de internet
                    composable("pantalla_inicio") {
                        PantallaListaGastos(
                            viewModel = miViewModel,
                            alIrAAjustes = { navegador.navigate("pantalla_config") }
                        )
                    }
                    // Ruta 2: Pantalla de ajustes para cambiar las preferencias del sistema
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
