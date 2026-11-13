package com.example.gestiondearriendos.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.gestiondearriendos.data.local.Reserva
import com.example.gestiondearriendos.ui.components.BarraSuperior
import com.example.gestiondearriendos.ui.components.CampoSeleccion
import com.example.gestiondearriendos.ui.components.DialogoFecha
import com.example.gestiondearriendos.ui.components.TituloSeccion
import com.example.gestiondearriendos.ui.theme.Ambar
import com.example.gestiondearriendos.util.ArchivoUtil
import com.example.gestiondearriendos.util.FormatoUtil
import com.example.gestiondearriendos.viewmodel.FinanzasViewModel
import java.io.File

@Composable
fun FormularioMovimientoScreen(
    esPago: Boolean,
    viewModel: FinanzasViewModel,
    onVolver: () -> Unit,
    reservasPorCobrar: List<Reserva> = emptyList()
) {
    var concepto by rememberSaveable { mutableStateOf("") }
    var monto by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable { mutableStateOf(FormatoUtil.hoy()) } // por defecto, hoy
    var reservaSeleccionada by remember { mutableStateOf<Reserva?>(null) }
    var menuAbierto by remember { mutableStateOf(false) }
    var mostrarFecha by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var rutaFoto by rememberSaveable { mutableStateOf<String?>(null) }
    var rutaPendiente by rememberSaveable { mutableStateOf<String?>(null) }

    val camaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito) {
            // Si se cambia la foto, borramos la anterior para no dejar archivos huérfanos
            rutaFoto?.let { File(it).delete() }
            rutaFoto = rutaPendiente
        } else {
            rutaPendiente?.let { File(it).delete() }
        }
    }

    fun abrirCamara() {
        val archivo = ArchivoUtil.crearArchivoImagen(context)
        rutaPendiente = archivo.absolutePath
        camaraLauncher.launch(ArchivoUtil.obtenerUri(context, archivo))
    }

    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) abrirCamara()
    }

    val mensaje by viewModel.mensaje.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val titulo = if (esPago) "Registrar pago" else "Registrar gasto"

    LaunchedEffect(mensaje) {
        mensaje?.let {
            val exito = it == "Pago registrado" || it == "Gasto registrado"
            if (exito) {
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
            soloFuturas = false, // un pago o gasto puede ser de hoy o de días anteriores
            onConfirmar = { fecha = it; mostrarFecha = false },
            onCancelar = { mostrarFecha = false }
        )
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = titulo,
                subtitulo = if (esPago) "Ingreso a la caja de la junta" else "Salida de la caja de la junta",
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
                    TituloSeccion("Detalle")

                    if (esPago) {
                        // Selector de reserva asociada (opcional)
                        Box {
                            CampoSeleccion(
                                valor = reservaSeleccionada?.id?.toString() ?: "",
                                textoVisible = reservaSeleccionada?.let {
                                    "${it.nombreVecino} · ${FormatoUtil.fechaLegible(it.fecha)}"
                                } ?: "Sin reserva (otro ingreso)",
                                etiqueta = "Reserva asociada (opcional)",
                                icono = Icons.Filled.EventAvailable,
                                onClick = { menuAbierto = true }
                            )
                            DropdownMenu(
                                expanded = menuAbierto,
                                onDismissRequest = { menuAbierto = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Sin reserva (otro ingreso)") },
                                    onClick = {
                                        reservaSeleccionada = null
                                        menuAbierto = false
                                    }
                                )
                                reservasPorCobrar.forEach { reserva ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(reserva.nombreVecino, style = MaterialTheme.typography.titleSmall)
                                                Text(
                                                    "${FormatoUtil.fechaLegible(reserva.fecha)} · ${reserva.horaInicio}–${reserva.horaFin}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            reservaSeleccionada = reserva
                                            if (concepto.isBlank()) concepto = "Arriendo sede"
                                            menuAbierto = false
                                        }
                                    )
                                }
                            }
                        }
                        if (reservasPorCobrar.isEmpty()) {
                            Text(
                                "No hay reservas confirmadas pendientes de pago.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedTextField(
                        value = concepto,
                        onValueChange = { concepto = it },
                        label = { Text("Concepto") },
                        placeholder = { Text(if (esPago) "Ej: Arriendo sede" else "Ej: Luz, agua, aseo") },
                        leadingIcon = { Icon(Icons.Filled.Description, contentDescription = null, tint = Ambar) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = monto,
                        // Solo se aceptan dígitos (sin puntos, comas ni letras)
                        onValueChange = { nuevo -> monto = nuevo.filter { it.isDigit() }.take(9) },
                        label = { Text("Monto en pesos") },
                        leadingIcon = { Icon(Icons.Filled.AttachMoney, contentDescription = null, tint = Ambar) },
                        supportingText = {
                            monto.toIntOrNull()?.let { Text("Se registrará como ${FormatoUtil.pesos(it)}") }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    )

                    CampoSeleccion(
                        valor = fecha,
                        textoVisible = FormatoUtil.fechaLegible(fecha),
                        etiqueta = "Fecha",
                        icono = Icons.Filled.CalendarMonth,
                        onClick = { mostrarFecha = true }
                    )
                }
            }

            // Comprobante / boleta
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TituloSeccion(if (esPago) "Comprobante" else "Boleta", detalle = "Opcional")

                    val imagen = remember(rutaFoto) {
                        rutaFoto?.let {
                            val opciones = BitmapFactory.Options().apply { inSampleSize = 4 }
                            BitmapFactory.decodeFile(it, opciones)?.asImageBitmap()
                        }
                    }
                    imagen?.let {
                        Image(
                            bitmap = it,
                            contentDescription = "Foto adjunta",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(MaterialTheme.shapes.medium)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val permitido = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                            if (permitido) abrirCamara() else permisoLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (rutaFoto == null) "Tomar foto" else "Cambiar foto")
                    }
                }
            }

            Button(
                onClick = {
                    if (esPago) viewModel.registrarPago(
                        concepto, monto, fecha,
                        reservaId = reservaSeleccionada?.id,
                        rutaComprobante = rutaFoto
                    )
                    else viewModel.registrarGasto(concepto, monto, fecha, rutaBoleta = rutaFoto)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (esPago) "Guardar pago" else "Guardar gasto")
            }
        }
    }
}
