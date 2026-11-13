package com.example.gestiondearriendos.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface GastoDao {

    @Insert
    suspend fun insertar(gasto: Gastos): Long

    @Delete
    suspend fun eliminar(gasto: Gastos)

    @Query("SELECT * FROM gastos ORDER BY fecha DESC")
    fun obtenerTodos(): Flow<List<Gastos>>

    @Query("SELECT COALESCE(SUM(monto), 0) FROM gastos")
    fun totalGastos(): Flow<Int>

    @Query("SELECT COALESCE(SUM(monto), 0) FROM gastos WHERE fecha LIKE :mes || '%'")
    fun totalGastosPorMes(mes: String): Flow<Int>
}