package com.helpme.redciudadana.model

data class Usuario(
    var uid: String = "",
    var nombre: String = "",
    var correo: String = "",
    var rol: String = "ciudadano",
    var fechaRegistro: Long = 0L
)
