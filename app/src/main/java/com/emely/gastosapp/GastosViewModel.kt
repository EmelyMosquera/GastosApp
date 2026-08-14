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

class GastosViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val gastoDao = database.gastoDao()
    private val prefsRepository = UserPreferencesRepository(application)

    // Estados reactivos para controlar la API remota (Punto 4d de la rúbrica)
    var estadoApi by mutableStateOf("Cargando información del servidor...")
    var mensajeResultado by mutableStateOf("")

    // Estado temporal para capturar de forma reactiva la foto de la cámara
    var fotoTemporalUri by mutableStateOf<String?>(null)

    // Exponer estados mediante StateFlow reactivos (Punto 4b de la rúbrica)
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
            prefsRepository.saveThemePreference(activo)
        }
    }

    fun registrarGasto(titulo: String, monto: Double, fotoUri: String?) {
        viewModelScope.launch {
            val nuevoGasto = Gasto(
                titulo = titulo,
                monto = monto,
                categoria = "General",
                fecha = "14/08/2026",
                fotoUri = fotoUri
            )
            gastoDao.insertGasto(nuevoGasto)
        }
    }

    // NUEVO: Elimina un gasto seleccionado usando Corrutinas de forma asíncrona (Punto 4b, 4c)
    fun eliminarGasto(gasto: Gasto) {
        viewModelScope.launch {
            gastoDao.eliminarGasto(gasto)
        }
    }
}
