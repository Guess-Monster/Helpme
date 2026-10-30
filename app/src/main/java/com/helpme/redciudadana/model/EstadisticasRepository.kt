package com.helpme.redciudadana.model

import com.google.firebase.database.*

class EstadisticasRepository {

    private val referencia = FirebaseDatabase.getInstance()
        .getReference("reportesPublicos")

    fun obtenerEstadisticas(
        callback: (Estadisticas?, String?) -> Unit
    ) {
        referencia.addListenerForSingleValueEvent(
            object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    var total = 0
                    var pendientes = 0
                    var enProceso = 0
                    var resueltos = 0

                    val tipos = mutableMapOf<String, Int>()

                    for (item in snapshot.children) {
                        val reporte = item.getValue(
                            ReportePublico::class.java
                        ) ?: continue

                        total++

                        when (reporte.estado.trim().lowercase()) {
                            "pendiente" -> pendientes++
                            "en proceso" -> enProceso++
                            "resuelto" -> resueltos++
                        }

                        val tipo = reporte.tipo.ifBlank { "Otro" }
                        tipos[tipo] = (tipos[tipo] ?: 0) + 1
                    }

                    callback(
                        Estadisticas(
                            total = total,
                            pendientes = pendientes,
                            enProceso = enProceso,
                            resueltos = resueltos,
                            porTipo = tipos.toSortedMap()
                        ),
                        null
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null, error.message)
                }
            }
        )
    }
}