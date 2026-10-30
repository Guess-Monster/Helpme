
package com.helpme.redciudadana.controller

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class UbicacionController(
    private val context: Context
) {

    private val cliente =
        LocationServices.getFusedLocationProviderClient(context)

    fun tienePermisos(): Boolean {
        val fino = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val aproximado = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fino || aproximado
    }

    @SuppressLint("MissingPermission")
    fun obtenerUbicacion(
        callback: (Double?, Double?, String?) -> Unit
    ) {
        if (!tienePermisos()) {
            callback(null, null, "Permiso de ubicación denegado")
            return
        }

        val token = CancellationTokenSource()

        cliente.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            token.token
        ).addOnSuccessListener { ubicacion ->

            if (ubicacion != null) {
                callback(
                    ubicacion.latitude,
                    ubicacion.longitude,
                    null
                )
            } else {
                callback(
                    null,
                    null,
                    "No se pudo obtener la ubicación. Activa el GPS."
                )
            }

        }.addOnFailureListener { error ->

            callback(
                null,
                null,
                error.localizedMessage
                    ?: "Error al obtener ubicación"
            )
        }
    }
}