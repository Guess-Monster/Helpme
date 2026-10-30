package com.helpme.redciudadana.model

data class Reporte(
    var id: String = "",
    var usuarioId: String = "",
    var tipo: String = "",
    var descripcion: String = "",
    var latitud: Double = 0.0,
    var longitud: Double = 0.0,
    var fotoUrl: String = "",
    var fotoPath: String = "",
    var estado: String = "Pendiente",
    var fechaCreacion: Long = 0L,
    var fechaActualizacion: Long = 0L,
    var actualizadoPor: String = ""
)
