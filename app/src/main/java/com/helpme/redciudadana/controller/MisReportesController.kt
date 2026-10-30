package com.helpme.redciudadana.controller

import com.helpme.redciudadana.model.MisReportesRepository
import com.helpme.redciudadana.model.Reporte

class MisReportesController {

    private val repository = MisReportesRepository()

    fun cargarReportes(
        callback: (List<Reporte>?, String?) -> Unit
    ) {
        repository.obtenerMisReportes(callback)
    }
}