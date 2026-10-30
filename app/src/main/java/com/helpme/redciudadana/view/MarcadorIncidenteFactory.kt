package com.helpme.redciudadana.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.util.Locale

object MarcadorIncidenteFactory {

    private data class Estilo(
        val color: Int,
        val simbolo: String
    )

    private fun obtenerEstilo(tipo: String): Estilo {
        return when (tipo.trim().lowercase(Locale.ROOT)) {
            "seguridad" -> Estilo(
                Color.rgb(220, 38, 38), "!"
            )
            "infraestructura" -> Estilo(
                Color.rgb(234, 88, 12), "⚒"
            )
            "animal perdido" -> Estilo(
                Color.rgb(124, 58, 237), "★"
            )
            "accidente" -> Estilo(
                Color.rgb(217, 119, 6), "✕"
            )
            "emergencia médica", "emergencia medica" -> Estilo(
                Color.rgb(219, 39, 119), "+"
            )
            "problema ambiental" -> Estilo(
                Color.rgb(22, 163, 74), "♣"
            )
            else -> Estilo(
                Color.rgb(37, 99, 235), "?"
            )
        }
    }

    fun crearMarcador(
        context: Context,
        tipo: String
    ): Drawable {

        val escala = context.resources.displayMetrics.density
        val ancho = (52 * escala).toInt().coerceAtLeast(52)
        val alto = (68 * escala).toInt().coerceAtLeast(68)

        val bitmap = Bitmap.createBitmap(
            ancho,
            alto,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        val estilo = obtenerEstilo(tipo)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val centroX = ancho / 2f
        val radio = ancho * 0.38f
        val centroY = alto * 0.35f

        // Sombra del marcador
        paint.color = Color.argb(65, 0, 0, 0)
        canvas.drawCircle(
            centroX + 2 * escala,
            centroY + 3 * escala,
            radio + 2 * escala,
            paint
        )

        // Punta del marcador
        val punta = Path().apply {
            moveTo(centroX - radio * 0.60f, centroY + radio * 0.55f)
            lineTo(centroX, alto - 3 * escala)
            lineTo(centroX + radio * 0.60f, centroY + radio * 0.55f)
            close()
        }

        paint.color = estilo.color
        paint.style = Paint.Style.FILL
        canvas.drawPath(punta, paint)

        // Círculo principal
        canvas.drawCircle(
            centroX,
            centroY,
            radio,
            paint
        )

        // Borde blanco
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3 * escala

        canvas.drawCircle(
            centroX,
            centroY,
            radio - 1.5f * escala,
            paint
        )

        paint.style = Paint.Style.FILL

        // Círculo interior semitransparente
        paint.color = Color.argb(45, 255, 255, 255)
        canvas.drawCircle(
            centroX,
            centroY,
            radio * 0.62f,
            paint
        )

        // Símbolo de categoría
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 23 * escala
        paint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )

        val medidas = paint.fontMetrics
        val baseTexto = centroY -
                (medidas.ascent + medidas.descent) / 2f

        canvas.drawText(
            estilo.simbolo,
            centroX,
            baseTexto,
            paint
        )

        return BitmapDrawable(
            context.resources,
            bitmap
        )
    }

    fun obtenerColor(tipo: String): Int {
        return obtenerEstilo(tipo).color
    }
}