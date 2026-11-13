package com.example.gestiondearriendos.util

object ValidadorMovimiento {

    private val regexFecha = Regex("""\d{4}-\d{2}-\d{2}""")

    fun validar (concepto : String, montoTexto: String, fecha: String): String?{
        if (concepto.isBlank()|| montoTexto.isBlank()|| fecha.isBlank())
            return "Completa todos los campos"

        val monto = montoTexto.toIntOrNull()
            ?: return "El monto tiene que ser un monto entero"
        if (monto <= 0) return "El monto tiene que ser mayor a 0"

        if (!regexFecha.matches(fecha)) return "La fecha debe tener el formato AAAA-MM-DD"
        return null
    }

}