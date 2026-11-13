package com.example.gestiondearriendos.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestiondearriendos.Model.EstadoReserva
import com.example.gestiondearriendos.Model.Rol
import com.example.gestiondearriendos.ui.screens.FormularioMovimientoScreen
import com.example.gestiondearriendos.ui.screens.FormularioReservaScreen
import com.example.gestiondearriendos.ui.screens.InicioScreen
import com.example.gestiondearriendos.ui.screens.ListaReservasScreen
import com.example.gestiondearriendos.ui.screens.SeleccionRolScreen
import com.example.gestiondearriendos.ui.screens.TransparenciaScreen
import com.example.gestiondearriendos.viewmodel.FinanzasViewModel
import com.example.gestiondearriendos.viewmodel.ReservaViewModel

@Composable
fun AppNavigation(reservaViewModel: ReservaViewModel, finanzasViewModel: FinanzasViewModel) {

    val navController = rememberNavController()
    val reservas by reservaViewModel.reservas.collectAsState()
    val pagos by finanzasViewModel.pagos.collectAsState()
    val saldo by finanzasViewModel.saldo.collectAsState()
    val idsPagados = pagos.mapNotNull { it.reservaId }.toSet()

    // Vuelve atrás solo si hay a dónde volver (evita quedar con la pantalla en blanco
    // si se presiona "volver" dos veces muy rápido)
    fun volver() {
        if (navController.previousBackStackEntry != null) navController.popBackStack()
    }

    NavHost(
        navController = navController,
        startDestination = "roles",
        // Transiciones suaves entre pantallas
        enterTransition = { slideInHorizontally { it / 4 } + fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutHorizontally { it / 4 } + fadeOut() }
    ) {

        composable("roles") {
            SeleccionRolScreen(onRol = { rol -> navController.navigate("inicio/${rol.name}") })
        }

        composable("inicio/{rol}") { entrada ->
            val rol = Rol.desdeTexto(entrada.arguments?.getString("rol"))
            InicioScreen(
                rol = rol,
                saldo = saldo,
                reservasPendientes = reservas.count { it.estado == EstadoReserva.PENDIENTE },
                onNavegar = { ruta -> navController.navigate(ruta) { launchSingleTop = true } },
                onCambiarRol = { navController.popBackStack("roles", false) }
            )
        }

        composable("lista/{rol}") { entrada ->
            val rol = Rol.desdeTexto(entrada.arguments?.getString("rol"))
            ListaReservasScreen(
                viewModel = reservaViewModel,
                puedeGestionar = rol == Rol.SECRETARIA,
                idsPagados = idsPagados,
                onNuevaReserva = { navController.navigate("formulario") { launchSingleTop = true } },
                onEditar = { id -> navController.navigate("editar/$id") { launchSingleTop = true } },
                onVolver = { volver() }
            )
        }

        composable("formulario") {
            FormularioReservaScreen(
                viewModel = reservaViewModel,
                onVolver = { volver() }
            )
        }

        // NUEVA ruta: editar una reserva existente
        composable(
            route = "editar/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entrada ->
            FormularioReservaScreen(
                viewModel = reservaViewModel,
                reservaId = entrada.arguments?.getInt("id"),
                onVolver = { volver() }
            )
        }

        composable("pago") {
            FormularioMovimientoScreen(
                esPago = true,
                viewModel = finanzasViewModel,
                onVolver = { volver() },
                reservasPorCobrar = reservas.filter {
                    it.estado == EstadoReserva.CONFIRMADA && it.id !in idsPagados
                }
            )
        }

        composable("gasto") {
            FormularioMovimientoScreen(
                esPago = false,
                viewModel = finanzasViewModel,
                onVolver = { volver() }
            )
        }

        composable("transparencia/{rol}") { entrada ->
            val rol = Rol.desdeTexto(entrada.arguments?.getString("rol"))
            TransparenciaScreen(
                viewModel = finanzasViewModel,
                puedeEliminar = rol == Rol.TESORERIA,
                onVolver = { volver() }
            )
        }
    }
}
