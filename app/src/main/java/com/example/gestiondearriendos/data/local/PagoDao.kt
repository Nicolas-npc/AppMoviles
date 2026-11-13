package com.example.gestiondearriendos.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao

interface PagoDao {

    @Insert
    suspend fun insertar(pago: Pago): Long

    @Delete
    suspend fun eliminar(pago: Pago)

    @Query("SELECT * FROM pagos ORDER BY fecha DESC")
    fun obtenerTodos(): Flow<List<Pago>>

    @Query("SELECT COALESCE(SUM(monto), 0) FROM pagos")
    fun totalIngresos(): Flow<Int>

    @Query("SELECT COALESCE(SUM(monto), 0) FROM pagos WHERE fecha LIKE :mes || '%'")
    fun totalIngresosPorMes(mes: String): Flow<Int>

}