package com.emely.gastosapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Entity
import kotlinx.coroutines.flow.Flow

// DAO: Interfaz que contiene las consultas SQL que usará la aplicación
@Dao
interface GastoDao {
    @Query("SELECT * FROM gastos_table ORDER BY id DESC")
    fun getAllGastos(): Flow<List<Gasto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGasto(gasto: Gasto)
}
