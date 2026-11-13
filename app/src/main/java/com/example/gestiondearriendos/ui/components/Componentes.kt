package com.example.gestiondearriendos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gestiondearriendos.Model.EstadoReserva
import com.example.gestiondearriendos.ui.theme.Ambar
import com.example.gestiondearriendos.ui.theme.AmbarClaro
import com.example.gestiondearriendos.ui.theme.AmbarOscuro
import com.example.gestiondearriendos.ui.theme.AmbarSuave
import com.example.gestiondearriendos.ui.theme.BeigeClaro
import com.example.gestiondearriendos.ui.theme.BeigeMedio
import com.example.gestiondearriendos.ui.theme.Terracota
import com.example.gestiondearriendos.ui.theme.TerracotaSuave
import com.example.gestiondearriendos.ui.theme.VerdeBosque
import com.example.gestiondearriendos.ui.theme.VerdeBosqueOscuro
import com.example.gestiondearriendos.ui.theme.VerdeNiebla
import com.example.gestiondearriendos.util.FormatoUtil

/**
 * Barra superior común a todas las pantallas: fondo verde bosque, texto beige
 * y un subtítulo opcional en ámbar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String,
    subtitulo: String? = null,
    onVolver: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Column {
                Text(titulo, style = MaterialTheme.typography.titleLarge)
                if (subtitulo != null) {
                    Text(
                        subtitulo,
                        style = MaterialTheme.typography.labelMedium,
                        color = AmbarClaro
                    )
                }
            }
        },
        navigationIcon = {
            if (onVolver != null) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = VerdeBosque,
            titleContentColor = BeigeClaro,
            navigationIconContentColor = BeigeClaro,
            actionIconContentColor = BeigeClaro
        )
    )
}

/** Círculo de color con un ícono dentro. */
@Composable
fun IconoCircular(
    icono: ImageVector,
    fondo: Color,
    colorIcono: Color,
    tamano: Int = 48
) {
    Box(
        modifier = Modifier
            .size(tamano.dp)
            .background(fondo, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size((tamano / 2).dp))
    }
}

/** Tarjeta de menú: ícono + título + descripción + flecha. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetaOpcion(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconoCircular(icono = icono, fondo = VerdeBosque, colorIcono = AmbarClaro)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                Text(
                    descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = Ambar
            )
        }
    }
}

/** "Píldora" de color con texto, usada para estados y etiquetas. */
@Composable
fun Etiqueta(
    texto: String,
    fondo: Color,
    colorTexto: Color,
    icono: ImageVector? = null
) {
    Surface(color = fondo, shape = RoundedCornerShape(50)) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icono != null) {
                Icon(icono, contentDescription = null, tint = colorTexto, modifier = Modifier.size(14.dp))
            }
            Text(texto, style = MaterialTheme.typography.labelMedium, color = colorTexto, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** Estado de la reserva con su color: ámbar = pendiente, verde = confirmada, terracota = rechazada. */
@Composable
fun ChipEstado(estado: EstadoReserva) {
    when (estado) {
        EstadoReserva.PENDIENTE ->
            Etiqueta(estado.etiqueta, AmbarSuave, AmbarOscuro, Icons.Filled.HourglassTop)
        EstadoReserva.CONFIRMADA ->
            Etiqueta(estado.etiqueta, VerdeNiebla, VerdeBosqueOscuro, Icons.Filled.CheckCircle)
        EstadoReserva.RECHAZADA ->
            Etiqueta(estado.etiqueta, TerracotaSuave, Terracota, Icons.Filled.Cancel)
    }
}

/** Avatar circular con las iniciales del vecino. */
@Composable
fun AvatarIniciales(nombre: String) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(VerdeBosque, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            FormatoUtil.iniciales(nombre),
            color = AmbarClaro,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

/** Fila pequeña con ícono + texto (fecha, hora, motivo...). */
@Composable
fun FilaDato(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = Ambar, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium)
    }
}

/** Título de sección con una línea ámbar debajo. */
@Composable
fun TituloSeccion(titulo: String, detalle: String? = null) {
    Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(titulo, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            if (detalle != null) {
                Text(detalle, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier
                .width(40.dp)
                .height(3.dp)
                .background(Ambar, RoundedCornerShape(50))
        )
    }
}

/** Mensaje amigable cuando una lista está vacía. */
@Composable
fun EstadoVacio(icono: ImageVector, titulo: String, descripcion: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconoCircular(icono = icono, fondo = BeigeMedio, colorIcono = VerdeBosque, tamano = 88)
        Spacer(Modifier.height(16.dp))
        Text(titulo, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(
            descripcion,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
