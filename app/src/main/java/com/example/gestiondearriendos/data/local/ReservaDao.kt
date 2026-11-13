package com.example.gestiondearriendos.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservaDao {

    @Insert
    suspend fun insertar (reserva : Reserva) : Long

    @Update
    suspend fun  actualizar (reserva: Reserva)

    @Delete
    suspend fun  eliminar (reserva: Reserva)

    @Query("SELECT * FROM reservas ORDER BY fecha, horaInicio")
    fun obtenerTodas(): Flow<List<Reserva>>

    @Query("SELECT * FROM reservas WHERE id = :id")
    suspend fun obtenerPorId(id: Int) : Reserva?

    @Query(
        """
        SELECT COUNT(*) FROM reservas
        WHERE fecha = :fecha
        AND estado = 'CONFIRMADA'
        AND horaInicio < :horaFin
        AND horaFin > :horaInicio
        """
    )
    suspend fun contarCruces(fecha: String, horaInicio: String, horaFin: String): Int

    @Query(
        """
        SELECT COUNT(*) FROM reservas
        WHERE id != :id
        AND fecha = :fecha
        AND estado = 'CONFIRMADA'
        AND horaInicio < :horaFin
        AND horaFin > :horaInicio
        """
    )
    suspend fun contarCrucesExcluyendo(id: Int, fecha: String, horaInicio: String, horaFin: String): Int
}