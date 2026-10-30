package com.helpme.redciudadana.view

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.view.View
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.helpme.redciudadana.R
import com.helpme.redciudadana.controller.CamaraController
import com.helpme.redciudadana.controller.ReporteController
import com.helpme.redciudadana.controller.UbicacionController

class CrearReporteActivity : AppCompatActivity() {

    private val reporteController = ReporteController()
    private lateinit var ubicacionController: UbicacionController
    private lateinit var camaraController: CamaraController

    private var latitud: Double? = null
    private var longitud: Double? = null
    private var fotoUri: Uri? = null
    private var uriPendiente: Uri? = null

    private lateinit var tvUbicacion: TextView
    private lateinit var imgReporte: ImageView
    private lateinit var btnEnviar: MaterialButton
    private lateinit var progress: ProgressBar

    private val permisoUbicacion =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permisos ->

            val autorizado = permisos.values.any { it }

            if (autorizado) {
                obtenerUbicacion()
            } else {
                mostrarMensaje("Debes autorizar la ubicación")
            }
        }

    private val tomarFoto =
        registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { resultado ->

            if (resultado && uriPendiente != null) {
                fotoUri = uriPendiente
                imgReporte.setImageURI(fotoUri)
            } else {
                mostrarMensaje("No se tomó la fotografía")
            }

            uriPendiente = null
        }

    override fun onDestroy() {
        super.onDestroy()

        if (isFinishing) {
            fotoUri?.let { uri ->
                try {
                    contentResolver.delete(uri, null, null)
                } catch (e: Exception) {
                    android.util.Log.w(
                        "HelpMeCamara",
                        "No se pudo eliminar la fotografía temporal",
                        e
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_reporte)

        if (FirebaseAuth.getInstance().currentUser == null) {
            mostrarMensaje("Debes iniciar sesión")
            finish()
            return
        }

        ubicacionController = UbicacionController(this)
        camaraController = CamaraController(this)

        tvUbicacion = findViewById(R.id.tvUbicacion)
        imgReporte = findViewById(R.id.imgReporte)
        btnEnviar = findViewById(R.id.btnEnviarReporte)
        progress = findViewById(R.id.progressReporte)

        val spTipo =
            findViewById<AutoCompleteTextView>(
                R.id.spTipoIncidente
            )

        val etDescripcion =
            findViewById<TextInputEditText>(
                R.id.etDescripcionReporte
            )

        val btnUbicacion =
            findViewById<MaterialButton>(
                R.id.btnObtenerUbicacion
            )

        val btnFoto =
            findViewById<MaterialButton>(
                R.id.btnTomarFoto
            )

        val tipos = listOf(
            "Seguridad",
            "Infraestructura",
            "Animal perdido",
            "Accidente",
            "Emergencia médica",
            "Problema ambiental",
            "Otro"
        )

        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            tipos
        )

        spTipo.setAdapter(adaptador)

        btnUbicacion.setOnClickListener {

            if (ubicacionController.tienePermisos()) {
                obtenerUbicacion()
            } else {
                permisoUbicacion.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }

        btnFoto.setOnClickListener {
            try {
                uriPendiente =
                    camaraController.crearUriFotografia()

                tomarFoto.launch(uriPendiente!!)
            } catch (e: Exception) {
                mostrarMensaje(
                    "No se pudo abrir la cámara: ${e.localizedMessage}"
                )
            }
        }

        btnEnviar.setOnClickListener {

            val tipo = spTipo.text.toString()
            val descripcion = etDescripcion.text.toString()

            progress.visibility = View.VISIBLE
            btnEnviar.isEnabled = false

            reporteController.crearReporte(
                tipo,
                descripcion,
                latitud,
                longitud
            ) { exitoso, mensaje ->

                progress.visibility = View.GONE
                btnEnviar.isEnabled = true

                mostrarMensaje(mensaje)

                if (exitoso) {
                    finish()
                }
            }
        }
    }

    private fun obtenerUbicacion() {

        tvUbicacion.text = "Obteniendo ubicación..."

        ubicacionController.obtenerUbicacion {
                lat, lon, error ->

            if (error != null) {
                latitud = null
                longitud = null
                tvUbicacion.text = error
                return@obtenerUbicacion
            }

            latitud = lat
            longitud = lon

            tvUbicacion.text =
                "Latitud: $lat\nLongitud: $lon"
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()
    }
}