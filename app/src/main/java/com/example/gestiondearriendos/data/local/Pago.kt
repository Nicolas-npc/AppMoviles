package com.example.gestiondearriendos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Pagos")
data class Pago (

    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val reservaId: Int? = null,
    val concepto: String,
    val monto: Int,
    val fecha: String,
    val rutaComprobante: String? = null
)