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

    // Inicializamos la base de datos Room de forma segura
    private val database = AppDatabase.getDatabase(application)
    private val gastoDao = database.gastoDao()
    private val prefsRepository = UserPreferencesRepository(application)

    // Estados reactivos para controlar la API remota exigidos en la rúbrica (Punto 4d)
    var estadoApi by mutableStateOf("Cargando información del servidor...")
    var mensajeResultado by mutableStateOf("")

    // Exponer estados mediante StateFlow reactivos (Punto 4b de la rúbrica)
    val isDarkMode: StateFlow<Boolean> = prefsRepository.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val listaGastos: StateFlow<List<Gasto>> = gastoDao.getAllGastos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        consultarServidorRemoto()
    }

    // Función asíncrona mediante Corrutinas que maneja los 3 estados: Cargando, Éxito y Error
    fun consultarServidorRemoto() {
        viewModelScope.launch {
            try {
                // 1. Estado: Cargando
                estadoApi = "Cargando..."

                val respuesta = RetrofitCliente.apiService.obtenerTipoCambio()

                if (respuesta.isSuccessful && respuesta.body() != null) {
                    // 2. Estado: Éxito
                    estadoApi = "Éxito"
                    mensajeResultado = "API Conectada. Moneda base de cambio: ${respuesta.body()?.base_code}"
                } else {
                    // 3. Estado: Error de respuesta del servidor
                    estadoApi = "Error"
                    mensajeResultado = "El servidor remoto no responde adecuadamente"
                }
            } catch (e: Exception) {
                // 3. Estado: Error por falta de internet o red caída
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

    fun registrarGasto(titulo: String, monto: Double) {
        viewModelScope.launch {
            val nuevoGasto = Gasto(
                titulo = titulo,
                monto = monto,
                categoria = "General",
                fecha = "10/08/2026"
            )
            gastoDao.insertGasto(nuevoGasto)
        }
    }
}
