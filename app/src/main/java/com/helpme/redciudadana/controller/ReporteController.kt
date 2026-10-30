package com.helpme.redciudadana.controller

import com.helpme.redciudadana.model.ReporteRepository

class ReporteController {

    private val repository = ReporteRepository()

    fun crearReporte(
        tipo: String,
        descripcion: String,
        latitud: Double?,
        longitud: Double?,
        callback: (Boolean, String) -> Unit
    ) {

        when {

            tipo.isBlank() -> {
                callback(
                    false,
                    "Selecciona un tipo de incidente"
                )
            }

            descripcion.trim().length < 10 -> {
                callback(
                    false,
                    "La descripción debe tener al menos 10 caracteres"
                )
            }

            descripcion.trim().length > 500 -> {
                callback(
                    false,
                    "La descripción no puede superar 500 caracteres"
                )
            }

            latitud == null || longitud == null -> {
                callback(
                    false,
                    "Debes obtener tu ubicación"
                )
            }

            latitud !in -90.0..90.0 ||
                    longitud !in -180.0..180.0 -> {
                callback(
                    false,
                    "Coordenadas inválidas"
                )
            }

            else -> {
                repository.guardarReporte(
                    tipo,
                    descripcion.trim(),
                    latitud,
                    longitud,
                    callback
                )
            }
        }
    }
}