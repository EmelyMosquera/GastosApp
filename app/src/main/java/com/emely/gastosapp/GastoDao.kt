package com.emely.gastosapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

// DAO: Interfaz con las operaciones SQL de la app de gastos
@Dao
interface GastoDao {
    @Query("SELECT * FROM gastos_table ORDER BY id DESC")
    fun getAllGastos(): Flow<List<Gasto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGasto(gasto: Gasto)

    // NUEVO: Permite eliminar de forma persistente un registro de la tabla Room
    @Delete
    suspend fun eliminarGasto(gasto: Gasto)
}
