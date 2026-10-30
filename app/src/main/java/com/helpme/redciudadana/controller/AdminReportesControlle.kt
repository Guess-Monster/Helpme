package com.helpme.redciudadana.controller

import com.helpme.redciudadana.model.AdminReportesRepository
import com.helpme.redciudadana.model.ReportePublico

class AdminReportesController {

    private val repository = AdminReportesRepository()

    fun verificarAdmin(
        callback: (Boolean) -> Unit
    ) {
        repository.verificarAdmin(callback)
    }

    fun listar(
        callback: (List<ReportePublico>?, String?) -> Unit
    ) {
        repository.listar(callback)
    }

    fun cambiarEstado(
        id: String,
        estado: String,
        callback: (Boolean, String) -> Unit
    ) {
        repository.cambiarEstado(
            id,
            estado,
            callback
        )
    }
}