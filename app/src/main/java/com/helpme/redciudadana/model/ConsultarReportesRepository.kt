package com.helpme.redciudadana.model

import com.google.firebase.database.*

class ConsultarReportesRepository {

    private val referencia = FirebaseDatabase.getInstance()
        .getReference("reportesPublicos")

    fun obtenerReportes(
        callback: (List<ReportePublico>?, String?) -> Unit
    ) {
        referencia.addListenerForSingleValueEvent(
            object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val lista = mutableListOf<ReportePublico>()

                    for (item in snapshot.children) {
                        val reporte = item.getValue(
                            ReportePublico::class.java
                        )

                        if (reporte != null) {
                            lista.add(reporte)
                        }
                    }

                    lista.sortByDescending { it.fechaCreacion }
                    callback(lista, null)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null, error.message)
                }
            }
        )
    }
}