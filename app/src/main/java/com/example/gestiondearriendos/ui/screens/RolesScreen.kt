package com.example.gestiondearriendos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestiondearriendos.Model.Rol


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun SeleccionRolScreen(onRol: (Rol) -> Unit){

    Scaffold(topBar = { TopAppBar(title = { Text("Junta de Vecinos") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("¿Cómo quieres ingresar?", style = MaterialTheme.typography.titleLarge)
            Button(onClick = { onRol(Rol.VECINO) }, modifier = Modifier.fillMaxWidth()) {
                Text("Vecino/a")
            }
            Button(onClick = { onRol(Rol.SECRETARIA) }, modifier = Modifier.fillMaxWidth()) {
                Text("Directiva / Secretaría")
            }
            Button(onClick = { onRol(Rol.TESORERIA) }, modifier = Modifier.fillMaxWidth()) {
                Text("Tesorería")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(rol: Rol, onNavegar: (String) -> Unit, onCambiarRol: () -> Unit) {

    Scaffold(topBar = { TopAppBar(title = { Text(rol.titulo) }) }) { padding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            when (rol){
                Rol.VECINO -> {
                    Button(onClick = { onNavegar("formulario") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Solicitar reserva")
                    }
                    Button(onClick = { onNavegar("lista/$rol") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Ver reservas")
                    }
                    Button(onClick = { onNavegar("transparencia/${rol.name}") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Transparencia financiera")
                    }
                }
                Rol.SECRETARIA -> {
                    Button(onClick = { onNavegar("lista/$rol") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Gestionar reservas")
                    }
                    Button(onClick = { onNavegar("transparencia/${rol.name}") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Seguimiento de gastos y recursos")
                    }
                }
                Rol.TESORERIA -> {
                    Button(onClick = { onNavegar("pago") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Registrar pago")
                    }
                    Button(onClick = { onNavegar("gasto") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Registrar gasto")
                    }
                    Button(onClick = { onNavegar("transparencia/${rol.name}") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Informe financiero")
                    }
                }
            }
            OutlinedButton(onClick = onCambiarRol, modifier = Modifier.fillMaxWidth()) {
                Text("Cambiar de rol")
            }
        }
    }
}
