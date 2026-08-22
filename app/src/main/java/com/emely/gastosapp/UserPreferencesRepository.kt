package com.emely.gastosapp

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Repositorio de preferencias requerido para el Modo Oscuro en la rúbrica (Punto 4c)
class UserPreferencesRepository(private val context: Context) {
    companion object {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    // Flujo asíncrono que lee el dataStore global definido en el MainActivity
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
