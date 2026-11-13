package com.example.gestiondearriendos.data.repository

import com.example.gestiondearriendos.data.local.Reserva
import com.example.gestiondearriendos.data.local.ReservaDao
import kotlinx.coroutines.flow.Flow

class ReservaRepository (private val dao: ReservaDao) {

    val reservas: Flow<List<Reserva>> = dao.obtenerTodas()

    suspend fun insertar(reserva: Reserva) = dao.insertar(reserva)

    suspend fun  actualizar(reserva: Reserva) = dao.actualizar(reserva)

    suspend fun  eliminar(reserva: Reserva) = dao.eliminar(reserva)

    suspend fun hayCruce(fecha: String, horaInicio: String,horaFin: String): Boolean{
        return dao.contarCruces(fecha,horaInicio,horaFin) > 0
    }
}