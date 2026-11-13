package com.example.gestiondearriendos.ui.screens

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestiondearriendos.viewmodel.ReservaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.example.gestiondearriendos.data.local.Reserva

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampoSeleccion(
    valor: String,
    etiqueta: String,
    onClick: () -> Unit,
    conIconoCalendario: Boolean = false
) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()

    LaunchedEffect(presionado) {
        if (presionado) onClick()
    }

    OutlinedTextField(
        value = valor,
        onValueChange = {},
        readOnly = true,
        label = { Text(etiqueta) },
        interactionSource = interaccion,
        trailingIcon = if (conIconoCalendario) {
            {
                IconButton(onClick = onClick) {
                    Icon(Icons.Default.DateRange, contentDescription = "Elegir fecha")
                }
            }
        } else null,
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoFecha(onConfirmar: (String) -> Unit, onCancelar: () -> Unit) {
    val ayer = System.currentTimeMillis() - 24L * 60 * 60 * 1000
    val estado = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis >= ayer
        }
    )

    DatePickerDialog(
        onDismissRequest = onCancelar,
        confirmButton = {
            TextButton(
                onClick = {
                    estado.selectedDateMillis?.let { millis ->
                        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        formato.timeZone = TimeZone.getTimeZone("UTC")
                        onConfirmar(formato.format(Date(millis)))
                    }
                },
                enabled = estado.selectedDateMillis != null
            ) { Text("Aceptar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    ) {
        DatePicker(state = estado)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoHora(onConfirmar: (String) -> Unit, onCancelar: () -> Unit) {
    val ahora = Calendar.getInstance()
    val estado = rememberTimePickerState(
        initialHour = ahora.get(Calendar.HOUR_OF_DAY),
        initialMinute = ahora.get(Calendar.MINUTE),
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onCancelar,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmar(String.format(Locale.US, "%02d:%02d", estado.hour, estado.minute))
                }
            ) { Text("Aceptar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } },
        text = { TimePicker(state = estado) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioReservaScreen(    viewModel: ReservaViewModel,onVolver: () -> Unit, reservaEditar: Reserva? = null) {

    val editando = reservaEditar != null
    var nombre by remember { mutableStateOf(reservaEditar?.nombreVecino ?: "") }
    var fecha by remember { mutableStateOf(reservaEditar?.fecha ?: "") }
    var horaInicio by remember { mutableStateOf(reservaEditar?.horaInicio ?: "") }
    var horaFin by remember { mutableStateOf(reservaEditar?.horaFin ?: "") }
    var motivo by remember { mutableStateOf(reservaEditar?.motivo ?: "") }

    var mostrarFecha by remember { mutableStateOf(false) }
    var mostrarHoraInicio by remember { mutableStateOf(false) }
    var mostrarHoraFin by remember { mutableStateOf(false) }

    val mensaje by viewModel.mensaje.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            if (it == "Solicitud enviada" || it == "Reserva actualizada") {
                viewModel.limpiarMensaje()
                onVolver()
            } else {
                snackbarHostState.showSnackbar(it)
                viewModel.limpiarMensaje()
            }
        }
    }

    if (mostrarFecha) {
        DialogoFecha(
            onConfirmar = { fecha = it; mostrarFecha = false },
            onCancelar = { mostrarFecha = false }
        )
    }
    if (mostrarHoraInicio) {
        DialogoHora(
            onConfirmar = { horaInicio = it; mostrarHoraInicio = false },
            onCancelar = { mostrarHoraInicio = false }
        )
    }
    if (mostrarHoraFin) {
        DialogoHora(
            onConfirmar = { horaFin = it; mostrarHoraFin = false },
            onCancelar = { mostrarHoraFin = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (editando) "Editar reserva" else "Nueva reserva") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del vecino/a") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            CampoSeleccion(
                valor = fecha,
                etiqueta = "Fecha",
                onClick = { mostrarFecha = true },
                conIconoCalendario = true
            )
            Spacer(Modifier.height(12.dp))
            CampoSeleccion(
                valor = horaInicio,
                etiqueta = "Hora de inicio",
                onClick = { mostrarHoraInicio = true }
            )
            Spacer(Modifier.height(12.dp))
            CampoSeleccion(
                valor = horaFin,
                etiqueta = "Hora de término",
                onClick = { mostrarHoraFin = true }
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                label = { Text("Motivo del arriendo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    if (reservaEditar != null) {
                        viewModel.editarReserva(reservaEditar, nombre, fecha, horaInicio, horaFin, motivo)
                    } else {
                        viewModel.solicitarReserva(nombre, fecha, horaInicio, horaFin, motivo)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (editando) "Guardar cambios" else "Solicitar reserva")
            }
        }
    }
}
