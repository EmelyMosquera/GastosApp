package com.emely.gastosapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// Entidad Room actualizada para almacenar de forma persistente la ruta de la foto
@Entity(tableName = "gastos_table")
data class Gasto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val monto: Double,
    val categoria: String,
    val fecha: String,
    val fotoUri: String? = null // NUEVO: Almacena la ubicación de la imagen de la cámara
)

// Modelo de datos para la API de internet exigido en el punto 4d de la rúbrica
data class TipoCambioRespuesta(
    // Enlaza el nombre original de la API con una variable estética nativa de Kotlin
    @SerializedName("base_code")
    val baseCode: String,

    val result: String
)

