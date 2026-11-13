package com.example.gestiondearriendos.ui.navigation

import androidx.compose.runtime.Composable
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
@Composable
fun AppNavigation (reservaViewModel: ReservaViewModel, finanzasViewModel: FinanzasViewModel){

    val navController = rememberNavController()
    val reservas by reservaViewModel.reservas.collectAsState()
    val pagos by finanzasViewModel.pagos.collectAsState()
    val idsPagados = pagos.mapNotNull { it.reservaId }.toSet()

    NavHost(navController = navController, startDestination = "roles") {

        composable("roles") {
            SeleccionRolScreen(onRol = { rol -> navController.navigate("inicio/$rol") })
        }

        composable("inicio/{rol}") { entrada ->
            val rol = entrada.arguments?.getString("rol") ?: "VECINO"
            InicioScreen(
                rol = rol,
                onNavegar = { ruta -> navController.navigate(ruta) },
                onCambiarRol = { navController.popBackStack("roles", false) }
            )
        }

        composable("lista/{rol}") { entrada ->
            val rol = entrada.arguments?.getString("rol") ?: "VECINO"
            ListaReservasScreen(
                viewModel = reservaViewModel,
                puedeGestionar = rol == "SECRETARIA",
                idsPagados = idsPagados,
                onNuevaReserva = {navController.navigate("formulario")}
            )
        }

        composable("formulario") {
            FormularioReservaScreen(
                viewModel = reservaViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable("pago") {
            FormularioMovimientoScreen(
                esPago = true,
                viewModel = finanzasViewModel,
                onVoler = { navController.popBackStack() },
                reservasPorCobrar = reservas.filter{
                    it.estado == "CONFIRMADA" && it.id  !in idsPagados
                }
            )
        }

        composable("gasto") {
            FormularioMovimientoScreen(
                esPago = false,
                viewModel = finanzasViewModel,
                onVoler = { navController.popBackStack() }
            )
        }

        composable("transparencia") {
            TransparenciaScreen(
                viewModel = finanzasViewModel,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
