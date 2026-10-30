package com.helpme.redciudadana.model

import com.google.firebase.database.*

class MapaRepository {

    private val database = FirebaseDatabase.getInstance()

    fun obtenerUbicaciones(
        callback: (List<UbicacionPublica>?, String?) -> Unit
    ) {
        database.getReference("ubicacionesPublicas")
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val lista = mutableListOf<UbicacionPublica>()

                        for (item in snapshot.children) {
                            val ubicacion = item.getValue(
                                UbicacionPublica::class.java
                            )

                            if (
                                ubicacion != null &&
                                ubicacion.latitud in -90.0..90.0 &&
                                ubicacion.longitud in -180.0..180.0
                            ) {
                                lista.add(ubicacion)
                            }
                        }

                        callback(lista, null)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(null, error.message)
                    }
                }
            )
    }

    fun obtenerReportes(
        callback: (List<ReportePublico>?, String?) -> Unit
    ) {
        database.getReference("reportesPublicos")
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val lista = mutableListOf<ReportePublico>()

                        for (item in snapshot.children) {
                            item.getValue(ReportePublico::class.java)
                                ?.let { lista.add(it) }
                        }

                        callback(lista, null)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(null, error.message)
                    }
                }
            )
    }
}