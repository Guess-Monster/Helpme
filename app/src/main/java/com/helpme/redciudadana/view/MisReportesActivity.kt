package com.helpme.redciudadana.view

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helpme.redciudadana.R
import com.helpme.redciudadana.controller.MisReportesController
import com.helpme.redciudadana.model.Reporte

class MisReportesActivity : AppCompatActivity() {

    private val controller = MisReportesController()

    private lateinit var recycler: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var txtVacio: TextView
    private lateinit var adapter: ReporteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mis_reportes)

        recycler = findViewById(R.id.recyclerReportes)
        progress = findViewById(R.id.progressReportes)
        txtVacio = findViewById(R.id.txtSinReportes)

        adapter = ReporteAdapter(mutableListOf())

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        cargarReportes()
    }

    private fun cargarReportes() {
        progress.visibility = View.VISIBLE
        txtVacio.visibility = View.GONE

        controller.cargarReportes { reportes, error ->

            if (isFinishing || isDestroyed) {
                return@cargarReportes
            }

            runOnUiThread {
                progress.visibility = View.GONE

                if (error != null) {
                    txtVacio.text = "Error: $error"
                    txtVacio.visibility = View.VISIBLE
                    adapter.actualizarReportes(emptyList())

                    Toast.makeText(
                        this,
                        error,
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    val lista = reportes.orEmpty()
                    adapter.actualizarReportes(lista)

                    txtVacio.text =
                        "Todavía no tienes reportes registrados."

                    txtVacio.visibility =
                        if (lista.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }
}