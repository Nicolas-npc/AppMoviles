package com.example.gestiondearriendos.Model

enum class Rol (val titulo: String){
    VECINO("Vecino/a"),
    SECRETARIA("Directiva / Secretaría"),
    TESORERIA("Tesorería");

    companion object{
        fun desdeTexto(texto: String?): Rol =
            entries.firstOrNull { it.name == texto } ?: VECINO
    }

}