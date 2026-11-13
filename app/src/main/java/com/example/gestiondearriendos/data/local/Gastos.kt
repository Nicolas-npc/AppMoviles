package com.example.gestiondearriendos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName =  "Gastos")
data class Gastos (

    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val concepto: String,
    val monto : Int,
    val fecha : String,
    val rutaBoleta: String?= null

)