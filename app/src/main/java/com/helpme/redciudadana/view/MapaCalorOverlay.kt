package com.helpme.redciudadana.view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay

class MapaCalorOverlay(
    private val ubicaciones: List<GeoPoint>
) : Overlay() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun draw(
        canvas: Canvas,
        mapView: MapView,
        shadow: Boolean
    ) {
        if (shadow || ubicaciones.isEmpty()) return

        val proyeccion = mapView.projection
        val escala = mapView.context.resources.displayMetrics.density

        val radio = 55f * escala

        for (ubicacion in ubicaciones) {
            val punto = proyeccion.toPixels(ubicacion, null)

            val gradiente = RadialGradient(
                punto.x.toFloat(),
                punto.y.toFloat(),
                radio,
                intArrayOf(
                    Color.argb(125, 220, 38, 38),
                    Color.argb(95, 249, 115, 22),
                    Color.argb(55, 250, 204, 21),
                    Color.TRANSPARENT
                ),
                floatArrayOf(
                    0f,
                    0.30f,
                    0.65f,
                    1f
                ),
                Shader.TileMode.CLAMP
            )

            paint.shader = gradiente

            canvas.drawCircle(
                punto.x.toFloat(),
                punto.y.toFloat(),
                radio,
                paint
            )
        }

        paint.shader = null
    }
}