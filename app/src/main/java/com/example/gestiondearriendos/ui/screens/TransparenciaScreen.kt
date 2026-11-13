package com.example.gestiondearriendos.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.gestiondearriendos.data.local.Gastos
import com.example.gestiondearriendos.data.local.Pago
import com.example.gestiondearriendos.ui.components.BarraSuperior
import com.example.gestiondearriendos.ui.components.DialogoConfirmacion
import com.example.gestiondearriendos.ui.components.IconoCircular
import com.example.gestiondearriendos.ui.components.TituloSeccion
import com.example.gestiondearriendos.ui.theme.AmbarClaro
import com.example.gestiondearriendos.ui.theme.BeigeClaro
import com.example.gestiondearriendos.ui.theme.TerracotaSuave
import com.example.gestiondearriendos.ui.theme.VerdeBosque
import com.example.gestiondearriendos.ui.theme.VerdeBosqueClaro
import com.example.gestiondearriendos.ui.theme.VerdeBosqueOscuro
import com.example.gestiondearriendos.ui.theme.VerdeSalvia
import com.example.gestiondearriendos.util.FormatoUtil
import com.example.gestiondearriendos.viewmodel.FinanzasViewModel

@Composable
fun TransparenciaScreen(viewModel: FinanzasViewModel, puedeEliminar: Boolean, onVolver: () -> Unit) {

    val ingresos by viewModel.totalIngresos.collectAsState()
    val totalGastos by viewModel.totalGastos.collectAsState()
    val saldo by viewModel.saldo.collectAsState()
    val pagos by viewModel.pagos.collectAsState()
    val gastos by viewModel.gastos.collectAsState()

    val mensaje by viewModel.mensaje.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var pagoAEliminar by remember { mutableStateOf<Pago?>(null) }
    var gastoAEliminar by remember { mutableStateOf<Gastos?>(null) }
    var fotoAMostrar by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensaje()
        }
    }

    pagoAEliminar?.let { pago ->
        DialogoConfirmacion(
            titulo = "Eliminar pago",
            texto = "¿Eliminar \"${pago.concepto}\" por ${FormatoUtil.pesos(pago.monto)}?",
            onConfirmar = { viewModel.eliminarPago(pago); pagoAEliminar = null },
            onCancelar = { pagoAEliminar = null }
        )
    }
    gastoAEliminar?.let { gasto ->
        DialogoConfirmacion(
            titulo = "Eliminar gasto",
            texto = "¿Eliminar \"${gasto.concepto}\" por ${FormatoUtil.pesos(gasto.monto)}?",
            onConfirmar = { viewModel.eliminarGasto(gasto); gastoAEliminar = null },
            onCancelar = { gastoAEliminar = null }
        )
    }
    fotoAMostrar?.let { ruta ->
        DialogoFoto(ruta = ruta, onCerrar = { fotoAMostrar = null })
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Transparencia financiera",
                subtitulo = "Cuentas claras de la junta",
                onVolver = onVolver
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                TarjetaSaldo(saldo = saldo, ingresos = ingresos, gastos = totalGastos)
            }

            item { TituloSeccion("Ingresos", detalle = "${pagos.size} movimientos") }
            if (pagos.isEmpty()) {
                item { TextoVacio("Sin ingresos registrados") }
            }
            items(pagos, key = { "pago-${it.id}" }) { pago ->
                FilaMovimiento(
                    esIngreso = true,
                    concepto = pago.concepto,
                    fecha = pago.fecha,
                    monto = pago.monto,
                    rutaFoto = pago.rutaComprobante,
                    puedeEliminar = puedeEliminar,
                    onVerFoto = { fotoAMostrar = pago.rutaComprobante },
                    onEliminar = { pagoAEliminar = pago }
                )
            }

            item { Spacer(Modifier.height(6.dp)) }
            item { TituloSeccion("Gastos", detalle = "${gastos.size} movimientos") }
            if (gastos.isEmpty()) {
                item { TextoVacio("Sin gastos registrados") }
            }
            items(gastos, key = { "gasto-${it.id}" }) { gasto ->
                FilaMovimiento(
                    esIngreso = false,
                    concepto = gasto.concepto,
                    fecha = gasto.fecha,
                    monto = gasto.monto,
                    rutaFoto = gasto.rutaBoleta,
                    puedeEliminar = puedeEliminar,
                    onVerFoto = { fotoAMostrar = gasto.rutaBoleta },
                    onEliminar = { gastoAEliminar = gasto }
                )
            }
        }
    }
}

/** Tarjeta grande con degradado verde: saldo + ingresos + gastos. */
@Composable
private fun TarjetaSaldo(saldo: Int, ingresos: Int, gastos: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(VerdeBosqueOscuro, VerdeBosque, VerdeBosqueClaro)))
                .padding(22.dp)
        ) {
            Text("SALDO DISPONIBLE", style = MaterialTheme.typography.labelSmall, color = VerdeSalvia)
            Text(
                FormatoUtil.pesos(saldo),
                style = MaterialTheme.typography.displaySmall,
                color = if (saldo < 0) TerracotaSuave else AmbarClaro
            )
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniResumen(
                    icono = Icons.AutoMirrored.Filled.TrendingUp,
                    titulo = "Ingresos",
                    monto = ingresos,
                    modifier = Modifier.weight(1f)
                )
                MiniResumen(
                    icono = Icons.AutoMirrored.Filled.TrendingDown,
                    titulo = "Gastos",
                    monto = gastos,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MiniResumen(icono: ImageVector, titulo: String, monto: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(BeigeClaro.copy(alpha = 0.12f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = AmbarClaro, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(titulo, style = MaterialTheme.typography.labelMedium, color = VerdeSalvia)
            Text(FormatoUtil.pesos(monto), style = MaterialTheme.typography.titleMedium, color = BeigeClaro)
        }
    }
}

/** Fila de un ingreso o un gasto. */
@Composable
private fun FilaMovimiento(
    esIngreso: Boolean,
    concepto: String,
    fecha: String,
    monto: Int,
    rutaFoto: String?,
    puedeEliminar: Boolean,
    onVerFoto: () -> Unit,
    onEliminar: () -> Unit
) {
    // Colores del tema: se adaptan solos al modo claro/oscuro
    val colorMonto: Color =
        if (esIngreso) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondaryContainer
    val fondoIcono: Color =
        if (esIngreso) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconoCircular(
                icono = if (esIngreso) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                fondo = fondoIcono,
                colorIcono = colorMonto,
                tamano = 42
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(concepto, style = MaterialTheme.typography.titleSmall)
                Text(
                    FormatoUtil.fechaLegible(fecha),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                (if (esIngreso) "+" else "−") + FormatoUtil.pesos(monto),
                style = MaterialTheme.typography.titleMedium,
                color = colorMonto
            )
            if (rutaFoto != null) {
                IconButton(onClick = onVerFoto) {
                    Icon(Icons.Filled.Photo, contentDescription = "Ver comprobante", tint = MaterialTheme.colorScheme.secondary)
                }
            }
            if (puedeEliminar) {
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun TextoVacio(texto: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(10.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** Muestra la foto del comprobante o boleta guardada. */
@Composable
private fun DialogoFoto(ruta: String, onCerrar: () -> Unit) {
    val imagen = remember(ruta) {
        val opciones = BitmapFactory.Options().apply { inSampleSize = 2 }
        BitmapFactory.decodeFile(ruta, opciones)?.asImageBitmap()
    }
    AlertDialog(
        onDismissRequest = onCerrar,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Comprobante", style = MaterialTheme.typography.titleLarge) },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (imagen != null) {
                    Image(
                        bitmap = imagen,
                        contentDescription = "Foto del comprobante",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                } else {
                    Text("No se encontró la imagen en el dispositivo.")
                }
            }
        },
        confirmButton = { TextButton(onClick = onCerrar) { Text("Cerrar") } }
    )
}
