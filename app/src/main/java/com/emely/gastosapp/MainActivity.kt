package com.emely.gastosapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emely.gastosapp.ui.theme.GastosAppTheme

// Declaración global única de DataStore vinculada al contexto raíz
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicialización segura del ViewModel
        val miViewModel = ViewModelProvider(this)[GastosViewModel::class.java]

        setContent {
            val modoOscuro by miViewModel.isDarkMode.collectAsState()

            GastosAppTheme(darkTheme = modoOscuro) {
                val navegador = rememberNavController()

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
