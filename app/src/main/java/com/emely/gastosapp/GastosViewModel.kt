package com.emely.gastosapp

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// CORRECCIÓN DEFINITIVA: Herencia estricta de AndroidViewModel recibiendo la propiedad application requerida en el paso de la Factory
class GastosViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val gastoDao = database.gastoDao()
    private val prefsRepository = UserPreferencesRepository(application)

    var estadoApi by mutableStateOf("Cargando información del servidor...")
    var mensajeResultado by mutableStateOf("")
    var fotoTemporalUri by mutableStateOf<String?>(null)

    val isDarkMode: StateFlow<Boolean> = prefsRepository.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val listaGastos: StateFlow<List<Gasto>> = gastoDao.getAllGastos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        consultarServidorRemoto()
    }

    fun consultarServidorRemoto() {
        viewModelScope.launch {
            try {
                estadoApi = "Cargando..."
                val respuesta = RetrofitCliente.apiService.obtenerTipoCambio()
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    estadoApi = "Éxito"
                    mensajeResultado = "API Conectada. Moneda base de cambio: ${respuesta.body()?.base_code}"
                } else {
                    estadoApi = "Error"
                    mensajeResultado = "El servidor remoto no responde adecuadamente"
                }
            } catch (e: Exception) {
                estadoApi = "Error"
                mensajeResultado = "Sin conexión a internet disponible"
            }
        }
    }

    fun cambiarModoOscuro(activo: Boolean) {
        viewModelScope.launch {
            try {
                prefsRepository.saveThemePreference(activo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun registrarGasto(titulo: String, monto: Double, fotoUri: String?) {
        viewModelScope.launch {
            try {
                val nuevoGasto = Gasto(
                    titulo = titulo,
                    monto = monto,
                    categoria = "General",
                    fecha = "14/08/2026",
                    fotoUri = fotoUri
                )
                gastoDao.insertGasto(nuevoGasto)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun eliminarGasto(gasto: Gasto) {
        viewModelScope.launch {
            try {
                gastoDao.eliminarGasto(gasto)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
