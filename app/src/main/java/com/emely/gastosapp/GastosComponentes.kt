package com.emely.gastosapp

import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Entity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// =================================================================
// PERSISTENCIA DE DATOS LOCALES CON ROOM (HISTORIAL DE GASTOS)
// =================================================================

// Entidad: Define la estructura de la tabla en la base de datos local
@Entity(tableName = "gastos_table")
data class Gasto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val monto: Double,
    val categoria: String,
    val fecha: String
)

// DAO (Data Access Object): Interfaz con las operaciones SQL de la app
@Dao
interface GastoDao {
    @Query("SELECT * FROM gastos_table ORDER BY id DESC")
    fun getAllGastos(): Flow<List<Gasto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGasto(gasto: Gasto)
}

// Database: Clase principal que inicializa y gestiona la base de datos Room
@Database(entities = [Gasto::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gastoDao(): GastoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gastos_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// =================================================================
// PERSISTENCIA DE PREFERENCIAS CON DATASTORE (AJUSTES DE USUARIO)
// =================================================================

// Instancia única global de DataStore vinculada al contexto de la app
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

// Repositorio encargado de leer y escribir la preferencia del Modo Oscuro
class UserPreferencesRepository(private val context: Context) {
    companion object {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    // Flujo asíncrono que emite cambios sobre el estado del Modo Oscuro
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { pref ->
        pref[IS_DARK_MODE] == true
    }

    // Guarda de forma persistente la preferencia elegida por el usuario
    suspend fun saveThemePreference(isDarkMode: Boolean) {
        context.dataStore.edit { pref ->
            pref[IS_DARK_MODE] = isDarkMode
        }
    }
}
// =================================================================
// VIEWMODEL: CAPA LOGICA CENTRAL DE LA APP (PATRON MVVM)
// =================================================================

class GastosViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val gastoDao = database.gastoDao()
    private val prefsRepository = UserPreferencesRepository(application)

    // Expone la preferencia del Modo Oscuro mediante un StateFlow reactivo
    val isDarkMode: StateFlow<Boolean> = prefsRepository.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Expone la lista de gastos locales actualizada en tiempo real
    val listaGastos: StateFlow<List<Gasto>> = gastoDao.getAllGastos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Modifica las preferencias del usuario usando Corrutinas
    fun cambiarModoOscuro(activo: Boolean) {
        viewModelScope.launch {
            prefsRepository.saveThemePreference(activo)
        }
    }

    // Registra un nuevo gasto de forma asíncrona dentro de Room
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

// =================================================================
// CAPA DE INTERFAZ DE USUARIO (VISTAS REACTIVAS CON JETPACK COMPOSE)
// =================================================================

// Pantalla Principal: Permite ingresar datos y ver el historial en una lista dinámica
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaGastos(viewModel: GastosViewModel, alIrAAjustes: () -> Unit) {
    val gastos by viewModel.listaGastos.collectAsState()
    var nombreGasto by remember { mutableStateOf("") }
    var precioGasto by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GastosApp - Inicio") },
                actions = {
                    Button(onClick = alIrAAjustes) { Text("Ajustes") }
                }
            )
        }
    ) { valoresPadding ->
        Column(modifier = Modifier.padding(valoresPadding).padding(16.dp)) {
            OutlinedTextField(
                value = nombreGasto,
                onValueChange = { nombreGasto = it },
                label = { Text("¿En qué gastaste?") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = precioGasto,
                onValueChange = { precioGasto = it },
                label = { Text("Monto ($)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (nombreGasto.isNotEmpty() && precioGasto.isNotEmpty()) {
                        val montoDouble = precioGasto.toDoubleOrNull() ?: 0.0
                        viewModel.registrarGasto(nombreGasto, montoDouble)
                        nombreGasto = ""
                        precioGasto = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Gasto Local")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Historial de Gastos (Room):", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Lista dinámica optimizada exigida en la rúbrica del proyecto
            LazyColumn {
                items(gastos) { gasto ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(gasto.titulo)
                            Text("$${gasto.monto}")
                        }
                    }
                }
            }
        }
    }
}

// Pantalla Opcional: Permite cambiar y guardar la configuración del Modo Oscuro
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAjustes(viewModel: GastosViewModel, alVolver: () -> Unit) {
    val modoOscuroActivo by viewModel.isDarkMode.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ajustes de la App") }) }
    ) { valoresPadding ->
        Column(modifier = Modifier.padding(valoresPadding).padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Activar Modo Oscuro", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = modoOscuroActivo,
                    onCheckedChange = { viewModel.cambiarModoOscuro(it) }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = alVolver, modifier = Modifier.fillMaxWidth()) {
                Text("Volver al Inicio")
            }
        }
    }
}
