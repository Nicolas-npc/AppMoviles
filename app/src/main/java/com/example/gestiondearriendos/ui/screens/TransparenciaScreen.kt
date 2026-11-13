package com.example.gestiondearriendos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestiondearriendos.viewmodel.FinanzasViewModel
import androidx.compose.material3.TextButton
import androidx.compose.runtime.setValue
import com.example.gestiondearriendos.data.local.Gastos
import com.example.gestiondearriendos.data.local.Pago
import com.example.gestiondearriendos.ui.components.DialogoConfirmacion

private fun  pesos (monto: Int ): String = "$" + monto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  TransparenciaScreen (viewModel: FinanzasViewModel, puedeEliminar: Boolean, onVolver: () -> Unit){

    val ingresos by viewModel.totalIngresos.collectAsState()
    val totalGastos by viewModel.totalGastos.collectAsState()
    val saldo by viewModel.saldo.collectAsState()
    val pagos by viewModel.pagos.collectAsState()
    val gastos by viewModel.gastos.collectAsState()

    val mensaje by viewModel.mensaje.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var pagoAEliminar by remember { mutableStateOf<Pago?>(null) }
    var gastoAEliminar by remember { mutableStateOf<Gastos?>(null) }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensaje()
        }
    }

    pagoAEliminar?.let { pago ->
        DialogoConfirmacion(
            titulo = "Eliminar pago",
            texto = "¿Eliminar \"${pago.concepto}\" por ${pesos(pago.monto)}?",
            onConfirmar = { viewModel.eliminarPago(pago); pagoAEliminar = null },
            onCancelar = { pagoAEliminar = null }
        )
    }
    gastoAEliminar?.let { gasto ->
        DialogoConfirmacion(
            titulo = "Eliminar gasto",
            texto = "¿Eliminar \"${gasto.concepto}\" por ${pesos(gasto.monto)}?",
            onConfirmar = { viewModel.eliminarGasto(gasto); gastoAEliminar = null },
            onCancelar = { gastoAEliminar = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transparencia financiera") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ){padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            item{
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Resumen", style = MaterialTheme.typography.titleMedium)
                        Text("Ingresos: ${pesos(ingresos)}")
                        Text("Gastos: ${pesos(totalGastos)}")
                        Text("Saldo: ${pesos(saldo)}", style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
            item{Text("Ingresos", style = MaterialTheme.typography.titleMedium)}
            if (pagos.isEmpty()){
                item { Text("sin ingresos registrados") }
            }
            items(pagos, key = {"pago-${it.id}"}){pago ->
                Card( modifier = Modifier.fillMaxWidth()){
                    Column(modifier = Modifier.padding(12.dp)){
                        Text(pago.concepto)
                        Text("${pago.fecha}  ·  ${pesos(pago.monto)}")
                    }
                    if (puedeEliminar) {
                        TextButton(onClick = { pagoAEliminar = pago }) { Text("Eliminar") }
                    }
                }
            }
            item { Text("Gastos", style = MaterialTheme.typography.titleMedium) }
            if (gastos.isEmpty()){
                item { Text("Sin gastos registrados") }
            }
            items(gastos, key ={"gasto-${it.id}"}){gasto ->
                Card(modifier = Modifier.fillMaxWidth()){
                    Column(modifier = Modifier.padding(12.dp)){
                        Text(gasto.concepto)
                        Text("${gasto.fecha}  ·  ${pesos(gasto.monto)}")
                    }
                }
                if (puedeEliminar) {
                    TextButton(onClick = { gastoAEliminar = gasto }) { Text("Eliminar") }
                }
            }
        }
    }
}