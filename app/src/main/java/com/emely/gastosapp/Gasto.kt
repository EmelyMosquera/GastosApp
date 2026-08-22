package com.emely.gastosapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "gastos_table")
data class Gasto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val monto: Double,
    val categoria: String,
    val fecha: String,
    val fotoUri: String? = null
)

data class TipoCambioRespuesta(
    @SerializedName("base_code") val baseCode: String = "USD",
    val result: String = "",
    val rates: Map<String, Double> = emptyMap()
)
