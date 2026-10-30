package com.helpme.redciudadana.controller

import com.helpme.redciudadana.model.Estadisticas
import com.helpme.redciudadana.model.EstadisticasRepository

class EstadisticasController {

    private val repository = EstadisticasRepository()

    fun cargar(
        callback: (Estadisticas?, String?) -> Unit
    ) {
        repository.obtenerEstadisticas(callback)
    }
}