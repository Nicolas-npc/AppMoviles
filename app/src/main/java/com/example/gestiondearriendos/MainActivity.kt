package com.example.gestiondearriendos

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestiondearriendos.ui.navigation.AppNavigation
import com.example.gestiondearriendos.ui.theme.GestionDeArriendosTheme
import com.example.gestiondearriendos.util.NotificacionUtil
import com.example.gestiondearriendos.viewmodel.FinanzasViewModel
import com.example.gestiondearriendos.viewmodel.ReservaViewModel

class MainActivity : ComponentActivity() {

    private val permisoNotificaciones = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // La barra de estado queda transparente con íconos claros,
        // porque arriba siempre hay una barra verde bosque.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))

        NotificacionUtil.crearCanal(this)

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            GestionDeArriendosTheme {
                val reservaViewModel: ReservaViewModel = viewModel()
                val finanzasViewModel: FinanzasViewModel = viewModel()
                AppNavigation(reservaViewModel, finanzasViewModel)
            }
        }
    }
}
