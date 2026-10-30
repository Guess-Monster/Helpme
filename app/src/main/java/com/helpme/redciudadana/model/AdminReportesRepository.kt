package com.helpme.redciudadana.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdminReportesRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    fun verificarAdmin(callback: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            callback(false)
            return
        }

        db.getReference("administradores")
            .child(uid)
            .get()
            .addOnSuccessListener {
                callback(it.getValue(Boolean::class.java) == true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun listar(
        callback: (List<ReportePublico>?, String?) -> Unit
    ) {
        db.getReference("reportesPublicos")
            .get()
            .addOnSuccessListener { snapshot ->
                val reportes = snapshot.children.mapNotNull {
                    it.getValue(ReportePublico::class.java)
                }.sortedByDescending { it.fechaCreacion }

                callback(reportes, null)
            }
            .addOnFailureListener {
                callback(null, it.localizedMessage)
            }
    }

    fun cambiarEstado(
        reporteId: String,
        nuevoEstado: String,
        callback: (Boolean, String) -> Unit
    ) {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            callback(false, "Sesión no válida")
            return
        }

        if (nuevoEstado !in listOf(
                "Pendiente", "En proceso", "Resuelto"
            )
        ) {
            callback(false, "Estado no válido")
            return
        }

        verificarAdmin { autorizado ->
            if (!autorizado) {
                callback(false, "No tienes permisos de administrador")
                return@verificarAdmin
            }

            val referencia = db.getReference("reportesPublicos")
                .child(reporteId)

            referencia.get()
                .addOnSuccessListener { snapshot ->
                    val reporte = snapshot.getValue(
                        ReportePublico::class.java
                    )

                    if (reporte == null || reporte.id != reporteId) {
                        callback(false, "Reporte no encontrado")
                        return@addOnSuccessListener
                    }

                    if (reporte.estado == nuevoEstado) {
                        callback(true, "El reporte ya tiene ese estado")
                        return@addOnSuccessListener
                    }

                    val fecha = System.currentTimeMillis()

                    val cambios = hashMapOf<String, Any>(
                        "/reportes/$reporteId/estado" to nuevoEstado,
                        "/reportes/$reporteId/fechaActualizacion" to fecha,
                        "/reportes/$reporteId/actualizadoPor" to uid,
                        "/reportesPublicos/$reporteId/estado" to nuevoEstado
                    )

                    db.reference.updateChildren(cambios)
                        .addOnSuccessListener {
                            callback(true, "Estado actualizado correctamente")
                        }
                        .addOnFailureListener {
                            callback(
                                false,
                                it.localizedMessage ?: "Error de Firebase"
                            )
                        }
                }
                .addOnFailureListener {
                    callback(
                        false,
                        it.localizedMessage ?: "Error al consultar"
                    )
                }
        }
    }
}