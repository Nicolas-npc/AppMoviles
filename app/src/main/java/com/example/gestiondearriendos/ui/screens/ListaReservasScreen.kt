package com.example.gestiondearriendos.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestiondearriendos.Model.EstadoReserva
import com.example.gestiondearriendos.data.local.Reserva
import com.example.gestiondearriendos.ui.components.AvatarIniciales
import com.example.gestiondearriendos.ui.components.BarraSuperior
import com.example.gestiondearriendos.ui.components.ChipEstado
import com.example.gestiondearriendos.ui.components.DialogoConfirmacion
import com.example.gestiondearriendos.ui.components.Etiqueta
import com.example.gestiondearriendos.ui.components.EstadoVacio
import com.example.gestiondearriendos.ui.components.FilaDato
import com.example.gestiondearriendos.ui.theme.AmbarClaro
import com.example.gestiondearriendos.ui.theme.AmbarOscuro
import com.example.gestiondearriendos.ui.theme.AmbarSuave
import com.example.gestiondearriendos.ui.theme.BeigeClaro
import com.example.gestiondearriendos.ui.theme.VerdeBosque
import com.example.gestiondearriendos.ui.theme.VerdeBosqueOscuro
import com.example.gestiondearriendos.ui.theme.VerdeNiebla
import com.example.gestiondearriendos.util.FormatoUtil
import com.example.gestiondearriendos.viewmodel.ReservaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaReservasScreen(
    viewModel: ReservaViewModel,
    puedeGestionar: Boolean,
    idsPagados: Set<Int>,
    onNuevaReserva: () -> Unit,
    onEditar: (Int) -> Unit,
    onVolver: () -> Unit
) {
    val reservas by viewModel.reservas.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // null = "Todas"
    var filtro by rememberSaveable { mutableStateOf<EstadoReserva?>(null) }
    val visibles = if (filtro == null) reservas else reservas.filter { it.estado == filtro }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = if (puedeGestionar) "Gestionar reservas" else "Reservas de la sede",
                subtitulo = "${reservas.size} en total",
                onVolver = onVolver
            )
        },
        floatingActionButton = {
            if (!puedeGestionar) {
                ExtendedFloatingActionButton(
                    onClick = onNuevaReserva,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("Nueva reserva") }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filtros por estado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChipFiltro("Todas (${reservas.size})", filtro == null) { filtro = null }
                EstadoReserva.entries.forEach { estado ->
                    val cantidad = reservas.count { it.estado == estado }
                    ChipFiltro("${estado.etiqueta} ($cantidad)", filtro == estado) { filtro = estado }
                }
            }

            if (visibles.isEmpty()) {
                EstadoVacio(
                    icono = Icons.Filled.EventBusy,
                    titulo = if (reservas.isEmpty()) "Aún no hay reservas" else "Nada por aquí",
                    descripcion = when {
                        reservas.isNotEmpty() -> "No hay reservas con el estado seleccionado."
                        puedeGestionar -> "Cuando un vecino solicite la sede, aparecerá en esta lista."
                        else -> "Toca \"Nueva reserva\" para solicitar la sede."
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    // Espacio extra abajo para que el botón flotante no tape la última tarjeta
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(visibles, key = { it.id }) { reserva ->
                        ReservaCard(
                            reserva = reserva,
                            puedeGestionar = puedeGestionar,
                            pagada = reserva.id in idsPagados,
                            onConfirmar = { viewModel.confirmarReserva(reserva) },
                            onRechazar = { viewModel.rechazarReserva(reserva) },
                            onEliminar = { viewModel.eliminarReserva(reserva) },
                            onEditar = { onEditar(reserva.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChipFiltro(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = { Text(texto) },
        leadingIcon = if (seleccionado) {
            { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = VerdeBosque,
            selectedLabelColor = BeigeClaro,
            selectedLeadingIconColor = AmbarClaro
        )
    )
}

@Composable
fun ReservaCard(
    reserva: Reserva,
    puedeGestionar: Boolean,
    pagada: Boolean,
    onConfirmar: () -> Unit,
    onRechazar: () -> Unit,
    onEliminar: () -> Unit,
    onEditar: () -> Unit
) {
    var confirmarEliminar by remember { mutableStateOf(false) }

    if (confirmarEliminar) {
        DialogoConfirmacion(
            titulo = "Eliminar reserva",
            texto = "¿Eliminar la reserva de ${reserva.nombreVecino} del ${FormatoUtil.fechaLegible(reserva.fecha)}?",
            onConfirmar = { confirmarEliminar = false; onEliminar() },
            onCancelar = { confirmarEliminar = false }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Encabezado: avatar + nombre + estado
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarIniciales(reserva.nombreVecino)
                Spacer(Modifier.width(12.dp))
                Text(
                    reserva.nombreVecino,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                ChipEstado(reserva.estado)
            }

            Spacer(Modifier.size(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FilaDato(Icons.Filled.Event, FormatoUtil.fechaLegible(reserva.fecha))
                FilaDato(Icons.Filled.Schedule, "${reserva.horaInicio} – ${reserva.horaFin}")
                FilaDato(Icons.AutoMirrored.Filled.Notes, reserva.motivo)
            }

            if (reserva.estado == EstadoReserva.CONFIRMADA) {
                Spacer(Modifier.size(12.dp))
                if (pagada) {
                    Etiqueta("Pagada", VerdeNiebla, VerdeBosqueOscuro, Icons.Filled.Paid)
                } else {
                    Etiqueta("Pendiente de pago", AmbarSuave, AmbarOscuro, Icons.Filled.MoneyOff)
                }
            }

            // Acciones (solo secretaría)
            if (puedeGestionar) {
                val puedeEditarOEliminar = !pagada
                val esPendiente = reserva.estado == EstadoReserva.PENDIENTE
                if (esPendiente || puedeEditarOEliminar) {
                    Spacer(Modifier.size(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.size(8.dp))
                }
                if (esPendiente) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(onClick = onConfirmar, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Confirmar")
                        }
                        OutlinedButton(onClick = onRechazar, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Rechazar")
                        }
                    }
                }
                if (puedeEditarOEliminar) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        if (reserva.estado != EstadoReserva.RECHAZADA) {
                            TextButton(onClick = onEditar) {
                                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Editar")
                            }
                        }
                        TextButton(onClick = { confirmarEliminar = true }) {
                            Icon(
                                Icons.Filled.DeleteOutline, contentDescription = null,
                                tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}
