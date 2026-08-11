package com.emely.gastosapp

import androidx.room.Entity
import androidx.room.PrimaryKey

// Modelo de datos local para la tabla de Room
@Entity(tableName = "gastos_table")
data class Gasto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val monto: Double,
    val categoria: String,
    val fecha: String
)

// Modelo de datos remoto para recibir la respuesta de la API de internet
data class TipoCambioRespuesta(
    val base_code: String,
    val result: String
)
