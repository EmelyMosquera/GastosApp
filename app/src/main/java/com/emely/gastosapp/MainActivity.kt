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

val Context.dataStore: DataStore<Preferences> by
preferencesDataStore(
    name = "user_settings"
)

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Se crea el ViewModel utilizando la Application del proyecto.
        val factory =
            ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(application)

        val gastosViewModel =
            ViewModelProvider(
                this,
                factory
            )[GastosViewModel::class.java]

        setContent {

            val modoOscuro by
            gastosViewModel
                .isDarkMode
                .collectAsState()

            GastosAppTheme(
                darkTheme = modoOscuro
            ) {

                val navController =
                    rememberNavController()

                NavHost(
                    navController =
                        navController,

                    startDestination =
                        "bienvenida"
                ) {

                    // Primera pantalla que presenta la aplicación.
                    composable(
                        route = "bienvenida"
                    ) {

                        PantallaBienvenida(

                            alComenzar = {

                                navController.navigate(
                                    "inicio"
                                ) {

                                    popUpTo(
                                        "bienvenida"
                                    ) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    // Panel principal.
                    composable(
                        route = "inicio"
                    ) {

                        PantallaInicio(
                            viewModel =
                                gastosViewModel,

                            alNuevoGasto = {

                                navController.navigate(
                                    "nuevo_gasto"
                                )
                            },

                            alHistorial = {

                                navController.navigate(
                                    "historial"
                                )
                            },

                            alEstadisticas = {

                                navController.navigate(
                                    "estadisticas"
                                )
                            },

                            alAjustes = {

                                navController.navigate(
                                    "ajustes"
                                )
                            }
                        )
                    }

                    // Formulario para registrar un nuevo gasto.
                    composable(
                        route = "nuevo_gasto"
                    ) {

                        PantallaNuevoGasto(
                            viewModel =
                                gastosViewModel,

                            alVolver = {
                                navController
                                    .popBackStack()
                            },

                            alGuardar = {

                                navController
                                    .popBackStack()
                            }
                        )
                    }

                    // Historial de movimientos.
                    composable(
                        route = "historial"
                    ) {

                        PantallaHistorial(
                            viewModel =
                                gastosViewModel,

                            alVolver = {
                                navController
                                    .popBackStack()
                            }
                        )
                    }

                    // Resumen estadístico.
                    composable(
                        route = "estadisticas"
                    ) {

                        PantallaEstadisticas(
                            viewModel =
                                gastosViewModel,

                            alVolver = {
                                navController
                                    .popBackStack()
                            }
                        )
                    }

                    // Preferencias de la aplicación.
                    composable(
                        route = "ajustes"
                    ) {

                        PantallaAjustes(
                            viewModel =
                                gastosViewModel,

                            alVolver = {
                                navController
                                    .popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}