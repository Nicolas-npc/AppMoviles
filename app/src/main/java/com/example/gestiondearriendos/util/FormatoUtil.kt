package com.example.gestiondearriendos.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

object FormatoUtil {

    private val localeChile = Locale("es", "CL")

    /** 15000 -> "$15.000"   |   -2500 -> "-$2.500" */
    fun pesos(monto: Int): String {
        val numero = String.format(localeChile, "%,d", abs(monto)).replace(',', '.')
        return if (monto < 0) "-$$numero" else "$$numero"
    }

    /** "2026-11-13" -> "vie 13 nov 2026". Si el texto no es válido, lo devuelve tal cual. */
    fun fechaLegible(fecha: String): String = try {
        val entrada = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }
        val salida = SimpleDateFormat("EEE d MMM yyyy", localeChile)
        salida.format(entrada.parse(fecha)!!).replace(".", "")
    } catch (e: Exception) {
        fecha
    }

    /** Fecha de hoy en formato AAAA-MM-DD (el que guarda la base de datos). */
    fun hoy(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    /** Iniciales para el avatar: "María López" -> "ML". */
    fun iniciales(nombre: String): String =
        nombre.trim().split(" ").filter { it.isNotBlank() }
            .take(2).joinToString("") { it.first().uppercase() }
            .ifBlank { "?" }
}
