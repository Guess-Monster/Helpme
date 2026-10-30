package com.helpme.redciudadana.view

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.helpme.redciudadana.R
import com.helpme.redciudadana.controller.AdminReportesController
import com.helpme.redciudadana.model.ReportePublico
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminReportesActivity : AppCompatActivity() {

    private val controller = AdminReportesController()

    private lateinit var contenedor: LinearLayout
    private lateinit var resumen: TextView
    private lateinit var progress: ProgressBar
    private lateinit var actualizar: Button

    private var autorizado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_reporte)

        contenedor = findViewById(R.id.contenedorAdminReportes)
        resumen = findViewById(R.id.txtAdminResumen)
        progress = findViewById(R.id.progressAdmin)
        actualizar = findViewById(R.id.btnAdminActualizar)

        actualizar.isEnabled = false
        actualizar.setOnClickListener { cargarReportes() }

        controller.verificarAdmin { esAdmin ->
            runOnUiThread {
                if (isFinishing || isDestroyed) return@runOnUiThread

                autorizado = esAdmin

                if (!esAdmin) {
                    Toast.makeText(
                        this,
                        "Acceso restringido a administradores",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    actualizar.isEnabled = true
                    cargarReportes()
                }
            }
        }
    }

    private fun cargarReportes() {
        if (!autorizado) return

        progress.visibility = View.VISIBLE
        actualizar.isEnabled = false
        resumen.text = "Cargando reportes..."

        controller.listar { reportes, error ->
            runOnUiThread {
                if (isFinishing || isDestroyed) return@runOnUiThread

                progress.visibility = View.GONE
                actualizar.isEnabled = true

                if (error != null || reportes == null) {
                    resumen.text = error ?: "Error al consultar reportes"
                    return@runOnUiThread
                }

                mostrarReportes(reportes)
            }
        }
    }

    private fun mostrarReportes(reportes: List<ReportePublico>) {
        contenedor.removeAllViews()
        resumen.text = "Total de reportes: ${reportes.size}"

        if (reportes.isEmpty()) {
            agregarTexto(
                contenedor,
                "No hay reportes disponibles.",
                16f,
                false
            )
            return
        }

        for (reporte in reportes) {
            crearTarjeta(reporte)
        }
    }

    private fun crearTarjeta(reporte: ReportePublico) {
        val tarjeta = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            setBackgroundColor(Color.WHITE)
            elevation = dp(3).toFloat()
        }

        val parametros = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = dp(12)
            leftMargin = dp(3)
            rightMargin = dp(3)
        }

        contenedor.addView(tarjeta, parametros)

        agregarTexto(tarjeta, reporte.tipo, 19f, true)
        agregarTexto(
            tarjeta,
            "Estado actual: ${reporte.estado}",
            14f,
            true
        )
        agregarTexto(tarjeta, reporte.descripcion, 15f, false)

        val fecha = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale("es", "GT")
        ).format(Date(reporte.fechaCreacion))

        agregarTexto(tarjeta, "Publicado: $fecha", 12f, false)

        val boton = Button(this).apply {
            text = "Cambiar estado"
            isAllCaps = false
            setOnClickListener {
                seleccionarEstado(reporte)
            }
        }

        tarjeta.addView(boton)
    }

    private fun seleccionarEstado(reporte: ReportePublico) {
        val opciones = arrayOf(
            "Pendiente",
            "En proceso",
            "Resuelto"
        )

        AlertDialog.Builder(this)
            .setTitle("Cambiar estado: ${reporte.tipo}")
            .setSingleChoiceItems(
                opciones,
                opciones.indexOf(reporte.estado)
            ) { dialogo, posicion ->
                dialogo.dismiss()

                val nuevoEstado = opciones[posicion]

                if (nuevoEstado == reporte.estado) return@setSingleChoiceItems

                AlertDialog.Builder(this)
                    .setTitle("Confirmar actualización")
                    .setMessage(
                        "¿Cambiar el estado de '${reporte.tipo}' " +
                                "a '$nuevoEstado'?"
                    )
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Confirmar") { _, _ ->
                        guardarEstado(reporte.id, nuevoEstado)
                    }
                    .show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarEstado(id: String, estado: String) {
        progress.visibility = View.VISIBLE
        actualizar.isEnabled = false

        controller.cambiarEstado(id, estado) { exito, mensaje ->
            runOnUiThread {
                if (isFinishing || isDestroyed) return@runOnUiThread

                progress.visibility = View.GONE
                actualizar.isEnabled = true

                Toast.makeText(
                    this,
                    mensaje,
                    Toast.LENGTH_LONG
                ).show()

                if (exito) cargarReportes()
            }
        }
    }

    private fun agregarTexto(
        padre: LinearLayout,
        contenido: String,
        tamano: Float,
        negrita: Boolean
    ) {
        val texto = TextView(this).apply {
            text = contenido
            textSize = tamano
            setTextColor(Color.parseColor("#0F172A"))
            setPadding(0, dp(4), 0, dp(6))

            if (negrita) {
                setTypeface(null, Typeface.BOLD)
            }
        }

        padre.addView(texto)
    }

    private fun dp(valor: Int): Int {
        return (valor * resources.displayMetrics.density).toInt()
    }
}