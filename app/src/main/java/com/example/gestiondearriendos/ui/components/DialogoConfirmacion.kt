package com.example.gestiondearriendos.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DialogoConfirmacion (
    titulo: String,
    texto: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
    textoConfirmar: String = "Eliminar"
){
    AlertDialog(
        onDismissRequest = onCancelar,
        icon = { Icon(Icons.Filled.DeleteOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
        title = { Text(titulo, style = MaterialTheme.typography.headlineSmall) },
        text = { Text(texto, style = MaterialTheme.typography.bodyLarge) },
        containerColor = MaterialTheme.colorScheme.surface,
        confirmButton = {
            Button(
                onClick = onConfirmar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) { Text(textoConfirmar) }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
