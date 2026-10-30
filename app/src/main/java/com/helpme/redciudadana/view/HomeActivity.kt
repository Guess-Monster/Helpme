package com.helpme.redciudadana.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.helpme.redciudadana.R
import com.helpme.redciudadana.controller.AuthController
import com.helpme.redciudadana.controller.AdminReportesController

class HomeActivity : AppCompatActivity() {

    private val authController = AuthController()
    private val adminController = AdminReportesController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!authController.haySesion()) {
            abrirLogin()
            return
        }

        setContentView(R.layout.activity_home)

        findViewById<MaterialButton>(R.id.btnCrearReporte)
            .setOnClickListener {
                abrir(CrearReporteActivity::class.java)
            }

        findViewById<MaterialButton>(R.id.btnVerReportes)
            .setOnClickListener {
                abrir(ConsultarReportesActivity::class.java)
            }

        val idMisReportes = resources.getIdentifier(
            "btnMisReportes",
            "id",
            packageName
        )

        if (idMisReportes != 0) {
            findViewById<View>(idMisReportes)
                ?.setOnClickListener {
                    abrir(MisReportesActivity::class.java)
                }
        }

        findViewById<MaterialButton>(R.id.btnVerMapa)
            .setOnClickListener {
                abrir(MapaActivity::class.java)
            }

        val botonEstadisticas =
            findViewById<MaterialButton>(R.id.btnEstadisticas)

        botonEstadisticas.setOnClickListener {
            abrir(EstadisticasActivity::class.java)
        }

        findViewById<MaterialButton>(R.id.btnCerrarSesion)
            .setOnClickListener {
                authController.cerrarSesion()
                abrirLogin()
            }

        configurarBotonAdministrador(botonEstadisticas)
    }

    private fun configurarBotonAdministrador(
        referencia: MaterialButton
    ) {
        adminController.verificarAdmin { esAdmin ->
            runOnUiThread {
                if (!esAdmin || isFinishing || isDestroyed) {
                    return@runOnUiThread
                }

                val padre = referencia.parent as? ViewGroup
                    ?: return@runOnUiThread

                val boton = MaterialButton(this).apply {
                    text = "Administración de reportes"
                    isAllCaps = false
                    setOnClickListener {
                        abrir(AdminReportesActivity::class.java)
                    }
                }

                val posicion = padre.indexOfChild(referencia)

                if (padre is LinearLayout) {
                    val parametros = LinearLayout.LayoutParams(
                        referencia.layoutParams.width,
                        referencia.layoutParams.height
                    )

                    if (referencia.layoutParams is LinearLayout.LayoutParams) {
                        val original =
                            referencia.layoutParams as LinearLayout.LayoutParams

                        parametros.setMargins(
                            original.leftMargin,
                            original.topMargin,
                            original.rightMargin,
                            original.bottomMargin
                        )

                        parametros.weight = original.weight
                    }

                    padre.addView(boton, posicion + 1, parametros)
                } else {
                    padre.addView(boton, posicion + 1)
                }
            }
        }
    }

    private fun abrir(destino: Class<*>) {
        startActivity(Intent(this, destino))
    }

    private fun abrirLogin() {
        val intent = Intent(this, LoginActivity::class.java)

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}