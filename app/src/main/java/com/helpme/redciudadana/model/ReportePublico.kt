package com.helpme.redciudadana.model

data class ReportePublico(
    val id: String = "",
    val tipo: String = "",
    val descripcion: String = "",
    val estado: String = "Pendiente",
    val fechaCreacion: Long = 0L
)