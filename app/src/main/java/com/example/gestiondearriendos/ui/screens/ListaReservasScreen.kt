package com.example.gestiondearriendos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestiondearriendos.Model.EstadoReserva
import com.example.gestiondearriendos.data.local.Reserva
import com.example.gestiondearriendos.ui.components.DialogoConfirmacion
import com.example.gestiondearriendos.viewmodel.ReservaViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaReservasScreen(viewModel: ReservaViewModel, puedeGestionar: Boolean,idsPagados: Set<Int>, onNuevaReserva: () -> Unit,onEditar: (Int) -> Unit){

    val reservas by viewModel.reservas.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        mensaje?.let{
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensaje()
        }
    }

    Scaffold (
        topBar = { TopAppBar(title = { Text("Reservas de la sede") }) },
        floatingActionButton  = {
            if (!puedeGestionar) {
                FloatingActionButton(onClick = onNuevaReserva) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva reserva")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState)}
    ){padding ->
        if (reservas.isEmpty()){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text("Aún no hay reservas. Presiona + para crear una.")
            }
        }else{
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                items(reservas, key = { it.id }) { reserva ->
                    ReservaCard(
                        reserva = reserva,
                        puedeGestionar = puedeGestionar,
                        pagada = reserva.id in idsPagados,
                        onConfirmar = { viewModel.confirmarReserva(reserva) },
                        onRechazar = { viewModel.rechazarReserva(reserva) } ,
                        onEliminar = { viewModel.eliminarReserva(reserva) },
                        onEditar = { onEditar(reserva.id) }
                    )
                }
            }
        }
    }
}
@Composable
fun ReservaCard(reserva: Reserva,puedeGestionar: Boolean,pagada: Boolean, onConfirmar: () -> Unit, onRechazar:()-> Unit,onEliminar: () -> Unit,onEditar: (Int) -> Unit){

    var confirmarEliminar by remember { mutableStateOf(false) }

    Card (modifier = Modifier.fillMaxWidth()){
        Column (modifier = Modifier.padding(16.dp)){
            Text(reserva.nombreVecino, style = MaterialTheme.typography.titleMedium)
            Text("${reserva.fecha}  ·  ${reserva.horaInicio} a ${reserva.horaFin}")
            Text("Motivo: ${reserva.motivo}")
            Text("Estado: ${reserva.estado.etiqueta}", style = MaterialTheme.typography.labelLarge)
            if (reserva.estado == EstadoReserva.CONFIRMADA) {
                    Text(
                        if (pagada) "Pago: Pagada" else "Pago: Pendiente de pago",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

            if (reserva.estado == EstadoReserva.PENDIENTE && puedeGestionar) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    Button(onClick = onConfirmar){Text("Confirmar")}
                    OutlinedButton(onClick = onRechazar) {Text("Rechazar") }
                }
            }
            if (puedeGestionar && !pagada) {
                Row {
                    if (reserva.estado != EstadoReserva.RECHAZADA) {
                        TextButton(onClick = onEditar) { Text("Editar") }
                    }
                    TextButton(onClick = { confirmarEliminar = true }) { Text("Eliminar") }
                }            }
            if (confirmarEliminar) {
                DialogoConfirmacion(
                    titulo = "Eliminar reserva",
                    texto = "¿Eliminar la reserva de ${reserva.nombreVecino} del ${reserva.fecha}?",
                    onConfirmar = { confirmarEliminar = false; onEliminar() },
                    onCancelar = { confirmarEliminar = false }
                )
            }
        }
    }
}