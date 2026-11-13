package com.example.gestiondearriendos.util

object ValidadorReserva {

    private val regexFecha = Regex("""\d{4}-\d{2}-\d{2}""")
    private val regexHora = Regex("""([01]\d|2[0-3]):[0-5]\d""")

    fun validar (
        nombre : String,
        fecha : String,
        horaInicio : String,
        horaFin : String,
        motivo : String
    ): String? {
        if (nombre.isBlank() || fecha.isBlank() || horaInicio.isBlank() || horaFin.isBlank() || motivo.isBlank()
            ) return "Completa todos los campos"

        if (!regexFecha.matches(fecha))
            return "La fecha debe tener formato AAAA-MM-DD"
        if (!regexHora.matches(horaInicio) || !regexHora.matches(horaFin))
            return "La hora debe tener formato HH:mm"
        if (horaFin <= horaInicio)
            return "La hora de término debe ser posterior a la de inicio"

        return null
    }


}
