package com.example.gestiondearriendos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondearriendos.data.local.AppDatabase
import com.example.gestiondearriendos.data.local.Gastos
import com.example.gestiondearriendos.data.local.Pago
import com.example.gestiondearriendos.data.repository.FianzasRepository
import com.example.gestiondearriendos.util.CalcularFinanzas
import com.example.gestiondearriendos.util.NotificacionUtil
import com.example.gestiondearriendos.util.ValidadorMovimiento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class FinanzasViewModel(application: Application): AndroidViewModel(application){

    private val db = AppDatabase.getDatabase(application)
    private val contexto = application.applicationContext
    private val repository = FianzasRepository(db.pagoDao(),db.gastoDao())
    private val inicio = SharingStarted.WhileSubscribed(5000)

    val pagos : StateFlow<List<Pago>> = repository.pagos.stateIn(viewModelScope,inicio,emptyList())

    val gastos : StateFlow<List<Gastos>> = repository.gastos.stateIn(viewModelScope, inicio,emptyList())

    val totalIngresos : StateFlow<Int> = repository.totalIngresos.stateIn(viewModelScope,inicio,0)

    val totalGastos : StateFlow<Int> = repository.totalGastos.stateIn(viewModelScope,inicio,0)

    val saldo : StateFlow<Int> =
        combine(totalIngresos,totalGastos) {ingresos, gastos ->
            CalcularFinanzas.saldo(ingresos, gastos)
        }.stateIn(viewModelScope,inicio, 0)

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun registrarPago(
        concepto: String,
        montoTexto : String,
        fecha: String,
        reservaId: Int? = null,
        rutaComprobante : String? = null
    ){
        val error = ValidadorMovimiento.validar(concepto,montoTexto,fecha)
        if (error != null ){
            _mensaje.value = error
            return
        }
        viewModelScope.launch {
            repository.registrarPago(
                Pago(
                    reservaId = reservaId,
                    concepto = concepto,
                    monto  = montoTexto.toInt(),
                    fecha = fecha,
                    rutaComprobante = rutaComprobante
                )
            )
            NotificacionUtil.mostrar(
                contexto,
                "Pago registrado",
                "Se registró el pago de $concepto por \$$montoTexto"
            )
            _mensaje.value = "Pago registrado"
        }
    }

    fun registrarGasto(
        concepto: String,
        montoTexto: String,
        fecha: String,
        rutaBoleta: String? = null
    ){
        val error = ValidadorMovimiento.validar(concepto,montoTexto,fecha)
        if (error != null){
            _mensaje.value = error
            return
        }
        viewModelScope.launch {
            repository.registrarGasto(
                Gastos(
                    concepto = concepto,
                    monto =  montoTexto.toInt(),
                    fecha =  fecha,
                    rutaBoleta = rutaBoleta
                )
            )
            _mensaje.value = "Gasto registrado"
        }
    }

    fun eliminarPago(pago: Pago){

        viewModelScope.launch {
            repository.eliminarPago(pago)
            pago.rutaComprobante?.let { ruta ->
                withContext(Dispatchers.IO){ File(ruta).delete() }
            }
            _mensaje.value = "Pago eliminado"
        }

    }

    fun eliminarGasto(gastos: Gastos){

        viewModelScope.launch {
            repository.eliminarGasto(gastos)
            gastos.rutaBoleta?.let { ruta ->
                withContext(Dispatchers.IO){ File(ruta).delete() }
            }
        }
    }


    fun  limpiarMensaje(){
        _mensaje.value = null
    }
}