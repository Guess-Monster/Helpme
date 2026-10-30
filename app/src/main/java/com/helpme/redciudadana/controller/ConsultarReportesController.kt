package com.helpme.redciudadana.controller

import com.helpme.redciudadana.model.ConsultarReportesRepository
import com.helpme.redciudadana.model.ReportePublico

class ConsultarReportesController {

    private val repository = ConsultarReportesRepository()

    fun consultar(
        callback: (List<ReportePublico>?, String?) -> Unit
    ) {
        repository.obtenerReportes(callback)
    }
}