package com.example.gestiondearriendos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestiondearriendos.ui.navigation.AppNavigation
import com.example.gestiondearriendos.ui.theme.GestionDeArriendosTheme
import com.example.gestiondearriendos.viewmodel.FinanzasViewModel
import com.example.gestiondearriendos.viewmodel.ReservaViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.gestiondearriendos.util.NotificacionUtil

class MainActivity : ComponentActivity() {
    private val permisoNotificaciones = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                val reservaViewModel: ReservaViewModel = viewModel ()
                val finanzasViewModel : FinanzasViewModel = viewModel()
                AppNavigation(reservaViewModel, finanzasViewModel)
            }
        }
    }
}
