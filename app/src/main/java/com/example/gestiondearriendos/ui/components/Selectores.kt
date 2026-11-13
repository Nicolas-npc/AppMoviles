package com.example.gestiondearriendos.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.gestiondearriendos.ui.theme.Ambar
import com.example.gestiondearriendos.ui.theme.AmbarSuave
import com.example.gestiondearriendos.ui.theme.TextoOscuro
import com.example.gestiondearriendos.ui.theme.VerdeBosque
import com.example.gestiondearriendos.ui.theme.BeigeClaro
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Campo de solo lectura que, al tocarlo, abre un selector (fecha u hora).
 * Se detecta el toque con un InteractionSource porque un TextField
 * "readOnly" se come el evento de click normal.
 */
@Composable
fun CampoSeleccion(
    valor: String,
    etiqueta: String,
    icono: ImageVector,
    onClick: () -> Unit,
    textoVisible: String = valor
) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()

    LaunchedEffect(presionado) {
        if (presionado) onClick()
    }

    OutlinedTextField(
        value = textoVisible,
        onValueChange = {},
        readOnly = true,
        label = { Text(etiqueta) },
        placeholder = { Text("Toca para elegir") },
        interactionSource = interaccion,
        leadingIcon = { Icon(icono, contentDescription = null, tint = Ambar) },
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Selector de fecha. Devuelve la fecha en formato AAAA-MM-DD.
 * @param soloFuturas si es true no deja elegir días pasados (útil para reservas).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoFecha(
    soloFuturas: Boolean,
    onConfirmar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    // El DatePicker trabaja en UTC, así que calculamos "hoy a medianoche UTC".
    val hoyUtc = remember {
        val local = Calendar.getInstance()
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
        }.timeInMillis
    }
    val estado = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                if (soloFuturas) utcTimeMillis >= hoyUtc else utcTimeMillis <= hoyUtc
        }
    )
    val colores = DatePickerDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surface,
        selectedDayContainerColor = VerdeBosque,
        selectedDayContentColor = BeigeClaro,
        todayDateBorderColor = Ambar,
        todayContentColor = VerdeBosque,
        selectedYearContainerColor = VerdeBosque,
        selectedYearContentColor = BeigeClaro
    )

    DatePickerDialog(
        onDismissRequest = onCancelar,
        colors = colores,
        confirmButton = {
            Button(
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
        DatePicker(state = estado, colors = colores)
    }
}

/** Selector de hora en formato 24 h. Devuelve "HH:mm". */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoHora(
    titulo: String,
    horaInicial: String,
    onConfirmar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    // Si ya había una hora elegida, el reloj parte desde ella.
    val partes = horaInicial.split(":").mapNotNull { it.toIntOrNull() }
    val ahora = Calendar.getInstance()
    val estado = rememberTimePickerState(
        initialHour = partes.getOrNull(0) ?: ahora.get(Calendar.HOUR_OF_DAY),
        initialMinute = partes.getOrNull(1) ?: 0,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(titulo, style = MaterialTheme.typography.titleLarge) },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmar(String.format(Locale.US, "%02d:%02d", estado.hour, estado.minute))
                }
            ) { Text("Aceptar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } },
        text = {
            TimePicker(
                state = estado,
                colors = TimePickerDefaults.colors(
                    clockDialColor = AmbarSuave,
                    selectorColor = VerdeBosque,
                    containerColor = MaterialTheme.colorScheme.surface,
                    timeSelectorSelectedContainerColor = VerdeBosque,
                    timeSelectorSelectedContentColor = BeigeClaro,
                    timeSelectorUnselectedContainerColor = AmbarSuave,
                    timeSelectorUnselectedContentColor = TextoOscuro,
                    clockDialSelectedContentColor = BeigeClaro,
                    clockDialUnselectedContentColor = TextoOscuro
                )
            )
        }
    )
}
