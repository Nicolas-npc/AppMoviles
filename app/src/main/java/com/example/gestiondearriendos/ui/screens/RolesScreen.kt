package com.example.gestiondearriendos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cottage
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gestiondearriendos.Model.Rol
import com.example.gestiondearriendos.ui.components.BarraSuperior
import com.example.gestiondearriendos.ui.components.TarjetaOpcion
import com.example.gestiondearriendos.ui.components.TituloSeccion
import com.example.gestiondearriendos.ui.theme.TerracotaSuave
import com.example.gestiondearriendos.ui.theme.AmbarClaro
import com.example.gestiondearriendos.ui.theme.BeigeClaro
import com.example.gestiondearriendos.ui.theme.VerdeBosque
import com.example.gestiondearriendos.ui.theme.VerdeBosqueClaro
import com.example.gestiondearriendos.ui.theme.VerdeBosqueOscuro
import com.example.gestiondearriendos.ui.theme.VerdeSalvia
import com.example.gestiondearriendos.util.FormatoUtil

/** Pantalla de bienvenida: el usuario elige con qué rol entra. */
@Composable
fun SeleccionRolScreen(onRol: (Rol) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Encabezado verde con degradado y logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(VerdeBosqueOscuro, VerdeBosque, VerdeBosqueClaro)),
                    RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)
                )
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(BeigeClaro.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Cottage,
                        contentDescription = null,
                        tint = AmbarClaro,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "Junta de Vecinos",
                    style = MaterialTheme.typography.displaySmall,
                    color = BeigeClaro,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Arriendo de la sede y finanzas transparentes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = VerdeSalvia,
                    textAlign = TextAlign.Center
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TituloSeccion("¿Cómo quieres ingresar?")
            TarjetaOpcion(
                icono = Icons.Filled.Groups,
                titulo = Rol.VECINO.titulo,
                descripcion = "Solicita la sede y revisa las cuentas de la junta",
                onClick = { onRol(Rol.VECINO) }
            )
            TarjetaOpcion(
                icono = Icons.Filled.AdminPanelSettings,
                titulo = Rol.SECRETARIA.titulo,
                descripcion = "Aprueba, edita o rechaza las reservas",
                onClick = { onRol(Rol.SECRETARIA) }
            )
            TarjetaOpcion(
                icono = Icons.Filled.AccountBalance,
                titulo = Rol.TESORERIA.titulo,
                descripcion = "Registra pagos, gastos y comprobantes",
                onClick = { onRol(Rol.TESORERIA) }
            )
        }
    }
}

/** Menú principal de cada rol, con un resumen arriba. */
@Composable
fun InicioScreen(
    rol: Rol,
    saldo: Int,
    reservasPendientes: Int,
    onNavegar: (String) -> Unit,
    onCambiarRol: () -> Unit
) {
    Scaffold(
        topBar = { BarraSuperior(titulo = rol.titulo, subtitulo = "Junta de Vecinos", onVolver = onCambiarRol) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TarjetaResumen(rol = rol, saldo = saldo, reservasPendientes = reservasPendientes)

            TituloSeccion("¿Qué quieres hacer?")

            when (rol) {
                Rol.VECINO -> {
                    TarjetaOpcion(Icons.Filled.CalendarMonth, "Solicitar reserva",
                        "Pide la sede para una fecha y horario", { onNavegar("formulario") })
                    TarjetaOpcion(Icons.Filled.EventAvailable, "Ver reservas",
                        "Revisa qué horarios están ocupados", { onNavegar("lista/${rol.name}") })
                    TarjetaOpcion(Icons.Filled.Visibility, "Transparencia financiera",
                        "Mira en qué se usa el dinero de la junta", { onNavegar("transparencia/${rol.name}") })
                }
                Rol.SECRETARIA -> {
                    TarjetaOpcion(Icons.Filled.TaskAlt, "Gestionar reservas",
                        "Confirma, rechaza o edita solicitudes", { onNavegar("lista/${rol.name}") })
                    TarjetaOpcion(Icons.Filled.Visibility, "Seguimiento de gastos",
                        "Ingresos, gastos y saldo actual", { onNavegar("transparencia/${rol.name}") })
                }
                Rol.TESORERIA -> {
                    TarjetaOpcion(Icons.Filled.Payments, "Registrar pago",
                        "Ingreso por arriendo u otro concepto", { onNavegar("pago") })
                    TarjetaOpcion(Icons.Filled.Receipt, "Registrar gasto",
                        "Luz, agua, mantención, compras…", { onNavegar("gasto") })
                    TarjetaOpcion(Icons.Filled.Savings, "Informe financiero",
                        "Detalle de movimientos y comprobantes", { onNavegar("transparencia/${rol.name}") })
                }
            }

            Spacer(Modifier.height(4.dp))
            OutlinedButton(
                onClick = onCambiarRol,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Filled.SwapHoriz, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cambiar de rol")
            }
        }
    }
}

/** Tarjeta verde con el saldo de la junta y, para secretaría, las reservas por revisar. */
@Composable
private fun TarjetaResumen(rol: Rol, saldo: Int, reservasPendientes: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = VerdeBosque),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(VerdeBosqueOscuro, VerdeBosqueClaro)))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    "SALDO DE LA JUNTA",
                    style = MaterialTheme.typography.labelSmall,
                    color = VerdeSalvia
                )
                Text(
                    FormatoUtil.pesos(saldo),
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (saldo < 0) TerracotaSuave else AmbarClaro
                )
                if (rol == Rol.SECRETARIA || rol == Rol.VECINO) {
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = BeigeClaro,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            when (reservasPendientes) {
                                0 -> "No hay solicitudes pendientes"
                                1 -> "1 solicitud pendiente de revisión"
                                else -> "$reservasPendientes solicitudes pendientes de revisión"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = BeigeClaro
                        )
                    }
                }
            }
        }
    }
}
