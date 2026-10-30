package com.helpme.redciudadana.model

data class Estadisticas(
    val total: Int = 0,
    val pendientes: Int = 0,
    val enProceso: Int = 0,
    val resueltos: Int = 0,
    val porTipo: Map<String, Int> = emptyMap()
)