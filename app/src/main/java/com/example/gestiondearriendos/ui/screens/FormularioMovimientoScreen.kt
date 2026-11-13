package com.example.gestiondearriendos.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.gestiondearriendos.util.ArchivoUtil
import com.example.gestiondearriendos.viewmodel.FinanzasViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import com.example.gestiondearriendos.data.local.Reserva

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun FormularioMovimientoScreen(
    esPago: Boolean,
    viewModel: FinanzasViewModel,
    onVoler: () -> Unit,
    reservasPorCobrar: List<Reserva> = emptyList()
){
    var concepto by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var reservaSeleccionada by remember { mutableStateOf<Reserva?>(null) }
    var menuAbierto by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var rutaFoto by rememberSaveable { mutableStateOf<String?>(null) }
    var rutaPendiente by rememberSaveable { mutableStateOf<String?>(null) }

    val camaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito) rutaFoto = rutaPendiente
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
    val snackbarHostState =  remember { SnackbarHostState() }
    val titulo = if (esPago) "Registrar pago" else "Registrar gasto"

    LaunchedEffect(mensaje) {
        mensaje?.let{
            val exito = it == "Pago registrado" || it == "Gasto registrado"
            if (exito){
                viewModel.limpiarMensaje()
                onVoler()
            }else{
                snackbarHostState.showSnackbar(it)
                viewModel.limpiarMensaje()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick =  onVoler) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = {  SnackbarHost(snackbarHostState)}
    ){ padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = concepto,
                onValueChange = { concepto = it },
                label = { Text(if (esPago) "Concepto (ej: Arriendo sede)" else "Concepto (ej: Luz, Agua)") },
                modifier = Modifier.fillMaxWidth()
            )
            if (esPago) {
                Box {
                    OutlinedButton(
                        onClick = { menuAbierto = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            reservaSeleccionada?.let { "${it.nombreVecino} · ${it.fecha}" }
                                ?: "Reserva asociada (opcional)"
                        )
                    }
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
                                    Text("${reserva.nombreVecino} · ${reserva.fecha} · ${reserva.horaInicio}-${reserva.horaFin}")
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
                    Text("No hay reservas confirmadas pendientes de pago")
                }
                Spacer(Modifier.height(12.dp))
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = monto,
                onValueChange = { monto = it },
                label = { Text("Monto (pesos)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha (AAAA-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    if (esPago) viewModel.registrarPago(
                        concepto, monto, fecha,
                        reservaId = reservaSeleccionada?.id,
                        rutaComprobante = rutaFoto
                    )
                    else viewModel.registrarGasto(concepto, monto, fecha, rutaBoleta = rutaFoto)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    val permitido = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                    if (permitido) abrirCamara() else permisoLauncher.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (rutaFoto == null) "Adjuntar foto" else "Cambiar foto")
            }

            val imagen = remember(rutaFoto) {
                rutaFoto?.let {
                    val opciones = BitmapFactory.Options().apply { inSampleSize = 4 }
                    BitmapFactory.decodeFile(it, opciones)?.asImageBitmap()
                }
            }
            imagen?.let {
                Spacer(Modifier.height(12.dp))
                Image(
                    bitmap = it,
                    contentDescription = "Foto adjunta",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
    }
}