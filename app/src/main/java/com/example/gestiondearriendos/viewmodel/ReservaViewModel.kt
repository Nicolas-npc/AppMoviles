package com.example.gestiondearriendos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondearriendos.Model.EstadoReserva
import com.example.gestiondearriendos.data.local.AppDatabase
import com.example.gestiondearriendos.data.local.Reserva
import com.example.gestiondearriendos.data.repository.ReservaRepository
import com.example.gestiondearriendos.util.NotificacionUtil
import com.example.gestiondearriendos.util.ValidadorReserva
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReservaViewModel (application: Application) : AndroidViewModel(application){

    private val repository = ReservaRepository(
        AppDatabase.getDatabase(application).reservaDao()
    )
    private val contexto = application.applicationContext

    val reservas: StateFlow<List<Reserva>> = repository.reservas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun solicitarReserva(
        nombre: String, fecha : String,horaInicio : String, horaFin: String, motivo : String){
        val error = ValidadorReserva.validar(nombre,fecha,horaInicio,horaFin,motivo)
        if (error !=null){
            _mensaje.value= error
            return
        }
        viewModelScope.launch {
            if (repository.hayCruce(fecha,horaInicio,horaFin)){
                _mensaje.value = "Ese horario ya está reservado"
            }else{
                repository.insertar(
                    Reserva(
                        nombreVecino = nombre,
                        fecha = fecha,
                        horaInicio = horaInicio,
                        horaFin = horaFin,
                        motivo = motivo
                    )
                )
                _mensaje.value = "Solicitud enviada"
            }
        }
    }

    fun confirmarReserva(reserva: Reserva){
        viewModelScope.launch {
            if (repository.hayCruce(reserva.fecha,reserva.horaInicio, reserva.horaFin)){
                _mensaje.value = "No se puede confirmar: se cruza con otra reserva"
            }else{
                repository.actualizar(reserva.copy(estado = EstadoReserva.CONFIRMADA))
                NotificacionUtil.mostrar(
                    contexto,
                    "Reserva confirmada",
                    "La reserva de ${reserva.nombreVecino} para el ${reserva.fecha} fue confirmada"
                )
                _mensaje.value = "Reserva confirmada"
            }
        }
    }

    fun rechazarReserva(reserva: Reserva){
        viewModelScope.launch {
            repository.actualizar(reserva.copy(estado = EstadoReserva.RECHAZADA))
            NotificacionUtil.mostrar(
                contexto,
                "Reserva rechazada",
                "La reserva de ${reserva.nombreVecino} para el ${reserva.fecha} fue rechazada"
            )
            _mensaje.value = "Reserva rechazada"
        }
    }

    fun eliminarReserva(reserva: Reserva){

        viewModelScope.launch {
            repository.eliminar(reserva)
            _mensaje.value = "Reserva eliminada"
        }
    }

    fun editarReserva(
        reserva: Reserva,
        nombre: String, fecha: String, horaInicio: String, horaFin: String, motivo: String
    ) {
        val error = ValidadorReserva.validar(nombre, fecha, horaInicio, horaFin, motivo)
        if (error != null) {
            _mensaje.value = error
            return
        }
        viewModelScope.launch {
            if (repository.hayCruceExcluyendo(reserva.id, fecha, horaInicio, horaFin)) {
                _mensaje.value = "Ese horario ya está reservado"
            } else {
                repository.actualizar(
                    reserva.copy(
                        nombreVecino = nombre,
                        fecha = fecha,
                        horaInicio = horaInicio,
                        horaFin = horaFin,
                        motivo = motivo
                    )
                )
                _mensaje.value = "Reserva actualizada"
            }
        }
    }
    fun limpiarMensaje(){
        _mensaje.value = null
    }

}