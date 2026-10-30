package com.helpme.redciudadana.view

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helpme.redciudadana.R
import com.helpme.redciudadana.controller.ConsultarReportesController
import com.helpme.redciudadana.model.ReportePublico

class ConsultarReportesActivity : AppCompatActivity() {

    private val controller = ConsultarReportesController()
    private val adapter = ReportePublicoAdapter()

    private lateinit var recycler: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var txtVacio: TextView
    private lateinit var spinnerTipo: Spinner
    private lateinit var spinnerEstado: Spinner

    private var todos = listOf<ReportePublico>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultar_reportes)

        recycler = findViewById(R.id.recyclerPublicos)
        progress = findViewById(R.id.progressPublicos)
        txtVacio = findViewById(R.id.txtSinPublicos)
        spinnerTipo = findViewById(R.id.spinnerTipo)
        spinnerEstado = findViewById(R.id.spinnerEstado)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        findViewById<Button>(R.id.btnActualizarReportes)
            .setOnClickListener { cargarReportes() }

        cargarReportes()
    }

    private fun cargarReportes() {
        progress.visibility = View.VISIBLE
        txtVacio.visibility = View.GONE

        controller.consultar { reportes, error ->
            runOnUiThread {
                if (isFinishing || isDestroyed) return@runOnUiThread

                progress.visibility = View.GONE

                if (error != null) {
                    txtVacio.text = "Error: $error"
                    txtVacio.visibility = View.VISIBLE
                    return@runOnUiThread
                }

                todos = reportes.orEmpty()
                configurarFiltros()
                aplicarFiltros()
            }
        }
    }

    private fun configurarFiltros() {
        val tipos = listOf("Todos los tipos") +
                todos.map { it.tipo }.distinct().sorted()

        val estados = listOf("Todos los estados") +
                todos.map { it.estado }.distinct().sorted()

        spinnerTipo.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            tipos
        )

        spinnerEstado.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            estados
        )

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                aplicarFiltros()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerTipo.onItemSelectedListener = listener
        spinnerEstado.onItemSelectedListener = listener
    }

    private fun aplicarFiltros() {
        val tipo = spinnerTipo.selectedItem?.toString()
            ?: "Todos los tipos"

        val estado = spinnerEstado.selectedItem?.toString()
            ?: "Todos los estados"

        val filtrados = todos.filter {
            (tipo == "Todos los tipos" || it.tipo == tipo) &&
                    (estado == "Todos los estados" || it.estado == estado)
        }

        adapter.actualizar(filtrados)

        txtVacio.text = "No hay reportes disponibles"
        txtVacio.visibility =
            if (filtrados.isEmpty()) View.VISIBLE else View.GONE
    }
}