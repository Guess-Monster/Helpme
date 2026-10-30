package com.helpme.redciudadana.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MisReportesRepository {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
        .getReference("reportes")

    fun obtenerMisReportes(
        callback: (List<Reporte>?, String?) -> Unit
    ) {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            callback(null, "Debes iniciar sesión")
            return
        }

        Log.d("HelpMeReportes", "Buscando reportes del UID: $uid")

        database.orderByChild("usuarioId")
            .equalTo(uid)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        Log.d(
                            "HelpMeReportes",
                            "Reportes encontrados: ${snapshot.childrenCount}"
                        )

                        val lista = mutableListOf<Reporte>()

                        for (item in snapshot.children) {
                            try {
                                val reporte = Reporte(
                                    id = item.child("id")
                                        .getValue(String::class.java)
                                        ?: item.key.orEmpty(),

                                    usuarioId = item.child("usuarioId")
                                        .getValue(String::class.java)
                                        .orEmpty(),

                                    tipo = item.child("tipo")
                                        .getValue(String::class.java)
                                        .orEmpty(),

                                    descripcion = item.child("descripcion")
                                        .getValue(String::class.java)
                                        .orEmpty(),

                                    latitud = item.child("latitud")
                                        .getValue(Double::class.java)
                                        ?: 0.0,

                                    longitud = item.child("longitud")
                                        .getValue(Double::class.java)
                                        ?: 0.0,

                                    fotoUrl = "",
                                    fotoPath = "",

                                    estado = item.child("estado")
                                        .getValue(String::class.java)
                                        ?: "Pendiente",

                                    fechaCreacion = item.child("fechaCreacion")
                                        .getValue(Long::class.java)
                                        ?: 0L,

                                    fechaActualizacion = item.child("fechaActualizacion")
                                        .getValue(Long::class.java)
                                        ?: 0L,

                                    actualizadoPor = item.child("actualizadoPor")
                                        .getValue(String::class.java)
                                        .orEmpty()
                                )

                                lista.add(reporte)

                            } catch (e: Exception) {
                                Log.e(
                                    "HelpMeReportes",
                                    "Error leyendo reporte",
                                    e
                                )
                            }
                        }

                        lista.sortByDescending { it.fechaCreacion }

                        callback(lista, null)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(
                            "HelpMeReportes",
                            "Error Firebase: ${error.message}"
                        )

                        callback(null, error.message)
                    }
                }
            )
    }
}