package com.emely.gastosapp

import androidx.room.Entity
import androidx.room.PrimaryKey

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

// Modelo de datos para la API de internet
data class TipoCambioRespuesta(
    val base_code: String,
    val result: String
)
