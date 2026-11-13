package com.example.gestiondearriendos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Reservas")

data class Reserva (

    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val nombreVecino: String,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val motivo: String,
    val estado: String = "PENDIENTE"

)