package com.helpme.redciudadana.controller

import com.helpme.redciudadana.model.*

class MapaController {

    private val repository = MapaRepository()

    fun cargarMarcadores(
        callback: (List<Pair<UbicacionPublica, ReportePublico>>?, String?) -> Unit
    ) {
        repository.obtenerUbicaciones { ubicaciones, error ->
            if (error != null) {
                callback(null, error)
                return@obtenerUbicaciones
            }

            repository.obtenerReportes { reportes, errorReportes ->
                if (errorReportes != null) {
                    callback(null, errorReportes)
                    return@obtenerReportes
                }

                val porId = reportes.orEmpty().associateBy { it.id }

                val marcadores = ubicaciones.orEmpty().mapNotNull {
                        ubicacion ->
                    val reporte = porId[ubicacion.id]

                    if (reporte != null) {
                        ubicacion to reporte
                    } else {
                        null
                    }
                }

                callback(marcadores, null)
            }
        }
    }
}