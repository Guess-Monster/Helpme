package com.helpme.redciudadana.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.helpme.redciudadana.R
import com.helpme.redciudadana.controller.EstadisticasController
import com.helpme.redciudadana.model.Estadisticas

class EstadisticasActivity : AppCompatActivity() {

    private val controller = EstadisticasController()

    private lateinit var total: TextView
    private lateinit var pendientes: TextView
    private lateinit var enProceso: TextView
    private lateinit var resueltos: TextView
    private lateinit var contenedor: LinearLayout
    private lateinit var progress: ProgressBar
    private lateinit var error: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas)

        total = findViewById(R.id.txtTotalEstadisticas)
        pendientes = findViewById(R.id.txtPendientes)
        enProceso = findViewById(R.id.txtEnProceso)
        resueltos = findViewById(R.id.txtResueltos)
        contenedor = findViewById(R.id.contenedorCategorias)
        progress = findViewById(R.id.progressEstadisticas)
        error = findViewById(R.id.txtErrorEstadisticas)

        findViewById<Button>(R.id.btnActualizarEstadisticas)
            .setOnClickListener { cargarDatos() }

        cargarDatos()
    }

    private fun cargarDatos() {
        progress.visibility = View.VISIBLE
        error.visibility = View.GONE

        controller.cargar { datos, mensaje ->
            runOnUiThread {
                if (isFinishing || isDestroyed) return@runOnUiThread

                progress.visibility = View.GONE

                if (mensaje != null || datos == null) {
                    error.text = mensaje ?: "Error desconocido"
                    error.visibility = View.VISIBLE
                    return@runOnUiThread
                }

                mostrarDatos(datos)
            }
        }
    }

    private fun mostrarDatos(datos: Estadisticas) {
        total.text = "Total de reportes: ${datos.total}"
        pendientes.text = "Pendientes: ${datos.pendientes}"
        enProceso.text = "En proceso: ${datos.enProceso}"
        resueltos.text = "Resueltos: ${datos.resueltos}"

        contenedor.removeAllViews()

        if (datos.total == 0) {
            val vacio = TextView(this)
            vacio.text = "No hay reportes publicados."
            contenedor.addView(vacio)
            return
        }

        val maximo = datos.porTipo.values.maxOrNull() ?: 1

        for ((tipo, cantidad) in datos.porTipo) {
            val etiqueta = TextView(this).apply {
                text = "$tipo: $cantidad"
                textSize = 15f
                setTextColor(Color.parseColor("#334155"))
                setPadding(0, 18, 0, 6)
            }

            val barra = ProgressBar(
                this,
                null,
                android.R.attr.progressBarStyleHorizontal
            ).apply {
                max = maximo
                progress = cantidad
                progressTintList =
                    android.content.res.ColorStateList.valueOf(
                        Color.parseColor("#6750A4")
                    )
            }

            contenedor.addView(etiqueta)
            contenedor.addView(
                barra,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    18
                )
            )
        }
    }
}