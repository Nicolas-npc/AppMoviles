package com.example.gestiondearriendos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestiondearriendos.ui.screens.FormularioMovimientoScreen
import com.example.gestiondearriendos.ui.screens.FormularioReservaScreen
import com.example.gestiondearriendos.ui.screens.InicioScreen
import com.example.gestiondearriendos.ui.screens.ListaReservasScreen
import com.example.gestiondearriendos.ui.screens.SeleccionRolScreen
import com.example.gestiondearriendos.ui.screens.TransparenciaScreen
import com.example.gestiondearriendos.viewmodel.FinanzasViewModel
import com.example.gestiondearriendos.viewmodel.ReservaViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.gestiondearriendos.Model.EstadoReserva
import com.example.gestiondearriendos.Model.Rol

@Composable
fun AppNavigation (reservaViewModel: ReservaViewModel, finanzasViewModel: FinanzasViewModel){

    val navController = rememberNavController()
    val reservas by reservaViewModel.reservas.collectAsState()
    val pagos by finanzasViewModel.pagos.collectAsState()
    val idsPagados = pagos.mapNotNull { it.reservaId }.toSet()

    NavHost(navController = navController, startDestination = "roles") {

        composable("roles") {
            SeleccionRolScreen(onRol = { rol -> navController.navigate("inicio/${rol.name}") })
        }

        composable("inicio/{rol}") { entrada ->
            val rol = Rol.desdeTexto(entrada.arguments?.getString("rol"))
            InicioScreen(
                rol = rol,
                onNavegar = { ruta -> navController.navigate(ruta) },
                onCambiarRol = { navController.popBackStack("roles", false) }
            )
        }

        composable("lista/{rol}") { entrada ->
            val rol =  Rol.desdeTexto(entrada.arguments?.getString("rol"))
            ListaReservasScreen(
                viewModel = reservaViewModel,
                puedeGestionar = rol == Rol.SECRETARIA,
                idsPagados = idsPagados,
                onNuevaReserva = {navController.navigate("formulario")},
                onEditar = { id -> navController.navigate("editar/$id") }
            )
        }

        composable("formulario") {
            FormularioReservaScreen(
                viewModel = reservaViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable("editar/{id}") { entrada ->
            val id = entrada.arguments?.getString("id")?.toIntOrNull()
            val reserva = reservas.firstOrNull { it.id == id }
            if (reserva == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
            } else {
                FormularioReservaScreen(
                    viewModel = reservaViewModel,
                    onVolver = { navController.popBackStack() },
                    reservaEditar = reserva
                )
            }
        }

        composable("pago") {
            FormularioMovimientoScreen(
                esPago = true,
                viewModel = finanzasViewModel,
                onVoler = { navController.popBackStack() },
                reservasPorCobrar = reservas.filter{
                    it.estado == EstadoReserva.CONFIRMADA && it.id !in idsPagados                }
            )
        }

        composable("gasto") {
            FormularioMovimientoScreen(
                esPago = false,
                viewModel = finanzasViewModel,
                onVoler = { navController.popBackStack() }
            )
        }

        composable("transparencia/{rol}") {entrada ->
            val rol= Rol.desdeTexto(entrada.arguments?.getString("rol"))
            TransparenciaScreen(
                viewModel = finanzasViewModel,
                puedeEliminar = rol == Rol.TESORERIA,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
