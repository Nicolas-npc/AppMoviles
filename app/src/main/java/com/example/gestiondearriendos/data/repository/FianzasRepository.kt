package com.example.gestiondearriendos.data.repository

import com.example.gestiondearriendos.data.local.GastoDao
import com.example.gestiondearriendos.data.local.Gastos
import com.example.gestiondearriendos.data.local.Pago
import com.example.gestiondearriendos.data.local.PagoDao

class FianzasRepository (

    private val pagoDao: PagoDao,
    private val gastoDao: GastoDao
){
    val pagos = pagoDao.obtenerTodos()
    val gastos = gastoDao.obtenerTodos()
    val totalIngresos = pagoDao.totalIngresos()
    val totalGastos = gastoDao.totalGastos()

    suspend fun registrarPago(pago: Pago)= pagoDao.insertar(pago)

    suspend fun registrarGasto(gastos: Gastos) = gastoDao.insertar(gastos)
}