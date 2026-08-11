package com.emely.gastosapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Instancia única global de DataStore vinculada al contexto de la app
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

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
