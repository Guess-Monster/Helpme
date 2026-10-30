package com.helpme.redciudadana.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.round

class ReporteRepository {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val reportesRef = database.getReference("reportes")

    fun guardarReporte(
        tipo: String,
        descripcion: String,
        latitud: Double,
        longitud: Double,
        callback: (Boolean, String) -> Unit
    ) {
        val usuario = auth.currentUser

        if (usuario == null) {
            callback(false, "Debes iniciar sesión")
            return
        }

        if (
            !latitud.isFinite() ||
            !longitud.isFinite() ||
            latitud !in -90.0..90.0 ||
            longitud !in -180.0..180.0
        ) {
            callback(false, "Ubicación no válida")
            return
        }

        val id = reportesRef.push().key

        if (id == null) {
            callback(false, "No se pudo generar el ID")
            return
        }

        val fecha = System.currentTimeMillis()

        val reportePrivado = Reporte(
            id = id,
            usuarioId = usuario.uid,
            tipo = tipo,
            descripcion = descripcion,
            latitud = latitud,
            longitud = longitud,
            fotoUrl = "",
            fotoPath = "",
            estado = "Pendiente",
            fechaCreacion = fecha,
            fechaActualizacion = fecha,
            actualizadoPor = ""
        )

        val reportePublico = ReportePublico(
            id = id,
            tipo = tipo,
            descripcion = descripcion,
            estado = "Pendiente",
            fechaCreacion = fecha
        )

        // Aproximar a una cuadrícula de ~1 km
        val latitudAprox = round(latitud * 100.0) / 100.0
        val longitudAprox = round(longitud * 100.0) / 100.0

        val ubicacionPublica = UbicacionPublica(
            id = id,
            latitud = latitudAprox,
            longitud = longitudAprox
        )

        val actualizaciones = hashMapOf<String, Any>(
            "/reportes/$id" to reportePrivado,
            "/reportesPublicos/$id" to reportePublico,
            "/ubicacionesPublicas/$id" to ubicacionPublica
        )

        database.reference.updateChildren(actualizaciones)
            .addOnSuccessListener {
                callback(
                    true,
                    "Reporte creado y publicado correctamente"
                )
            }
            .addOnFailureListener { error ->
                callback(
                    false,
                    "Error al guardar reporte: ${error.localizedMessage}"
                )
            }
    }
}