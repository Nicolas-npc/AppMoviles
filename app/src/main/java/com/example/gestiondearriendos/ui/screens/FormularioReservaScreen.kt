package com.example.gestiondearriendos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.gestiondearriendos.data.local.Reserva
import com.example.gestiondearriendos.ui.components.BarraSuperior
import com.example.gestiondearriendos.ui.components.CampoSeleccion
import com.example.gestiondearriendos.ui.components.DialogoFecha
import com.example.gestiondearriendos.ui.components.DialogoHora
import com.example.gestiondearriendos.ui.components.TituloSeccion
import com.example.gestiondearriendos.ui.theme.Ambar
import com.example.gestiondearriendos.ui.theme.AmbarOscuro
import com.example.gestiondearriendos.ui.theme.AmbarSuave
import com.example.gestiondearriendos.util.FormatoUtil
import com.example.gestiondearriendos.viewmodel.ReservaViewModel

/**
 * Formulario para crear una reserva nueva o, si viene [reservaId], editar una existente.
 */
@Composable
fun FormularioReservaScreen(
    viewModel: ReservaViewModel,
    onVolver: () -> Unit,
    reservaId: Int? = null
) {
    val esEdicion = reservaId != null

    // rememberSaveable: los datos no se pierden si se gira la pantalla
    var nombre by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable { mutableStateOf("") }
    var horaInicio by rememberSaveable { mutableStateOf("") }
    var horaFin by rememberSaveable { mutableStateOf("") }
    var motivo by rememberSaveable { mutableStateOf("") }
    var datosCargados by rememberSaveable { mutableStateOf(false) }
    var reservaOriginal by remember { mutableStateOf<Reserva?>(null) }

    var mostrarFecha by remember { mutableStateOf(false) }
    var mostrarHoraInicio by remember { mutableStateOf(false) }
    var mostrarHoraFin by remember { mutableStateOf(false) }

    val mensaje by viewModel.mensaje.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Modo edición: cargamos la reserva desde la base de datos
    LaunchedEffect(reservaId) {
        if (reservaId != null) {
            val r = viewModel.obtenerReserva(reservaId)
            reservaOriginal = r
            if (r != null && !datosCargados) {
                nombre = r.nombreVecino
                fecha = r.fecha
                horaInicio = r.horaInicio
                horaFin = r.horaFin
                motivo = r.motivo
                datosCargados = true
            }
        }
    }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            when (it) {
                "Solicitud enviada" -> {
                    viewModel.limpiarMensaje()
                    onVolver()
                }
                // Volvemos a la lista; ella misma mostrará el aviso "Reserva actualizada"
                "Reserva actualizada" -> onVolver()
                else -> {
                    snackbarHostState.showSnackbar(it)
                    viewModel.limpiarMensaje()
                }
            }
        }
    }

    if (mostrarFecha) {
        DialogoFecha(
            soloFuturas = true,
            onConfirmar = { fecha = it; mostrarFecha = false },
            onCancelar = { mostrarFecha = false }
        )
    }
    if (mostrarHoraInicio) {
        DialogoHora(
            titulo = "Hora de inicio",
            horaInicial = horaInicio,
            onConfirmar = { horaInicio = it; mostrarHoraInicio = false },
            onCancelar = { mostrarHoraInicio = false }
        )
    }
    if (mostrarHoraFin) {
        DialogoHora(
            titulo = "Hora de término",
            horaInicial = horaFin.ifBlank { horaInicio },
            onConfirmar = { horaFin = it; mostrarHoraFin = false },
            onCancelar = { mostrarHoraFin = false }
        )
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = if (esEdicion) "Editar reserva" else "Nueva reserva",
                subtitulo = "Sede de la junta de vecinos",
                onVolver = onVolver
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!esEdicion) {
                // Aviso informativo en ámbar
                Card(
                    colors = CardDefaults.cardColors(containerColor = AmbarSuave),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Info, contentDescription = null, tint = AmbarOscuro)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Tu solicitud quedará como Pendiente hasta que la directiva la confirme.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AmbarOscuro
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TituloSeccion("¿Quién solicita?")
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre del vecino/a") },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = Ambar) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TituloSeccion("¿Cuándo?")
                    CampoSeleccion(
                        valor = fecha,
                        textoVisible = if (fecha.isBlank()) "" else FormatoUtil.fechaLegible(fecha),
                        etiqueta = "Fecha",
                        icono = Icons.Filled.CalendarMonth,
                        onClick = { mostrarFecha = true }
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(Modifier.weight(1f)) {
                            CampoSeleccion(
                                valor = horaInicio,
                                etiqueta = "Desde",
                                icono = Icons.Filled.Schedule,
                                onClick = { mostrarHoraInicio = true }
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            CampoSeleccion(
                                valor = horaFin,
                                etiqueta = "Hasta",
                                icono = Icons.Filled.Schedule,
                                onClick = { mostrarHoraFin = true }
                            )
                        }
                    }

                    TituloSeccion("¿Para qué?")
                    OutlinedTextField(
                        value = motivo,
                        onValueChange = { motivo = it },
                        label = { Text("Motivo del arriendo") },
                        placeholder = { Text("Ej: cumpleaños, reunión, taller…") },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null, tint = Ambar) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        minLines = 2,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Button(
                onClick = {
                    val original = reservaOriginal
                    if (esEdicion && original != null) {
                        viewModel.editarReserva(original, nombre, fecha, horaInicio, horaFin, motivo)
                    } else if (!esEdicion) {
                        viewModel.solicitarReserva(nombre, fecha, horaInicio, horaFin, motivo)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Icon(
                    if (esEdicion) Icons.Filled.Save else Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(if (esEdicion) "Guardar cambios" else "Solicitar reserva")
            }
        }
    }
}
