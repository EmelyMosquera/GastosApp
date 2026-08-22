package com.emely.gastosapp

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GastosViewModel(application: Application) : AndroidViewModel(application) {

    private val gastoDao = AppDatabase.getDatabase(application).gastoDao()
    private val prefsRepository = UserPreferencesRepository(application)

    var estadoApi by mutableStateOf("Cargando...")
        private set
    var mensajeResultado by mutableStateOf("Consultando tipo de cambio")
        private set
    var fotoTemporalUri by mutableStateOf<String?>(null)
        private set

    val isDarkMode: StateFlow<Boolean> = prefsRepository.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val listaGastos: StateFlow<List<Gasto>> = gastoDao.getAllGastos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        consultarServidorRemoto()
    }

    fun consultarServidorRemoto() {
        viewModelScope.launch {
            estadoApi = "Cargando..."
            mensajeResultado = "Consultando información financiera"
            try {
                val respuesta = RetrofitCliente.apiService.obtenerTipoCambio()
                val body = respuesta.body()
                if (respuesta.isSuccessful && body != null) {
                    val eur = body.rates["EUR"]
                    estadoApi = "Conectado"
                    mensajeResultado = if (eur != null) {
                        "1 ${body.baseCode} = ${String.format(Locale.US, "%.3f", eur)} EUR"
                    } else {
                        "Servicio remoto disponible · Base ${body.baseCode}"
                    }
                } else {
                    estadoApi = "Sin respuesta"
                    mensajeResultado = "No se pudo obtener el tipo de cambio"
                }
            } catch (_: Exception) {
                estadoApi = "Sin conexión"
                mensajeResultado = "Tus gastos locales siguen disponibles"
            }
        }
    }

    fun cambiarModoOscuro(activo: Boolean) {
        viewModelScope.launch {
            prefsRepository.saveThemePreference(activo)
        }
    }

    fun guardarFoto(bitmap: Bitmap) {
        viewModelScope.launch {
            val ruta = withContext(Dispatchers.IO) {
                val carpeta = File(getApplication<Application>().filesDir, "recibos")
                if (!carpeta.exists()) carpeta.mkdirs()
                val archivo = File(carpeta, "recibo_${System.currentTimeMillis()}.jpg")
                FileOutputStream(archivo).use { salida ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, salida)
                }
                archivo.absolutePath
            }
            fotoTemporalUri = ruta
        }
    }

    fun quitarFotoTemporal() {
        fotoTemporalUri = null
    }

    fun registrarGasto(titulo: String, monto: Double, categoria: String) {
        viewModelScope.launch {
            val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            gastoDao.insertGasto(
                Gasto(
                    titulo = titulo.trim(),
                    monto = monto,
                    categoria = categoria,
                    fecha = fechaActual,
                    fotoUri = fotoTemporalUri
                )
            )
            fotoTemporalUri = null
        }
    }

    fun eliminarGasto(gasto: Gasto) {
        viewModelScope.launch {
            gastoDao.eliminarGasto(gasto)
            gasto.fotoUri?.let { ruta ->
                withContext(Dispatchers.IO) { runCatching { File(ruta).delete() } }
            }
        }
    }
}
