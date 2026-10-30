package com.helpme.redciudadana.view

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.helpme.redciudadana.R
import com.helpme.redciudadana.controller.MapaController
import com.helpme.redciudadana.model.ReportePublico
import com.helpme.redciudadana.model.UbicacionPublica
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MapaActivity : AppCompatActivity() {

    private val controller = MapaController()

    private lateinit var mapa: MapView
    private lateinit var contador: TextView
    private lateinit var progress: ProgressBar
    private lateinit var spinnerTipo: Spinner
    private lateinit var spinnerEstado: Spinner
    private lateinit var btnCambiarVista: Button
    private lateinit var txtLeyenda: TextView
    private lateinit var scrollLeyenda: HorizontalScrollView

    private var modoCalor = false

    private var todosLosReportes =
        listOf<Pair<UbicacionPublica, ReportePublico>>()

    private val tipos = listOf(
        "Todos los tipos",
        "Seguridad",
        "Infraestructura",
        "Animal perdido",
        "Accidente",
        "Emergencia médica",
        "Problema ambiental",
        "Otro"
    )

    private val estados = listOf(
        "Todos los estados",
        "Pendiente",
        "En proceso",
        "Resuelto"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue =
            applicationContext.packageName

        setContentView(R.layout.activity_map)

        mapa = findViewById(R.id.mapaIncidentes)
        contador = findViewById(R.id.txtContadorMapa)
        progress = findViewById(R.id.progressMapa)
        spinnerTipo = findViewById(R.id.spinnerTipoMapa)
        spinnerEstado = findViewById(R.id.spinnerEstadoMapa)
        btnCambiarVista = findViewById(R.id.btnCambiarVistaMapa)
        txtLeyenda = findViewById(R.id.txtLeyendaMapa)
        scrollLeyenda = findViewById(R.id.scrollLeyendaMapa)

        configurarMapa()
        configurarFiltros()

        findViewById<Button>(R.id.btnActualizarMapa)
            .setOnClickListener {
                cargarMapa()
            }

        btnCambiarVista.setOnClickListener {
            modoCalor = !modoCalor
            aplicarFiltros()
        }

        cargarMapa()
    }

    private fun configurarMapa() {
        mapa.setTileSource(TileSourceFactory.MAPNIK)
        mapa.setMultiTouchControls(true)
        mapa.controller.setZoom(12.0)
        mapa.controller.setCenter(GeoPoint(15.4700, -90.3700))
    }

    private fun configurarFiltros() {
        val adapterTipos = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            tipos
        )
        adapterTipos.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerTipo.adapter = adapterTipos

        val adapterEstados = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            estados
        )
        adapterEstados.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerEstado.adapter = adapterEstados

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

    private fun cargarMapa() {
        progress.visibility = View.VISIBLE
        contador.text = "Cargando incidentes..."

        controller.cargarMarcadores { datos, error ->
            runOnUiThread {
                if (isFinishing || isDestroyed) {
                    return@runOnUiThread
                }

                progress.visibility = View.GONE

                if (error != null) {
                    contador.text = "Error: $error"
                    return@runOnUiThread
                }

                todosLosReportes = datos.orEmpty()
                aplicarFiltros()
            }
        }
    }

    private fun aplicarFiltros() {
        if (!::mapa.isInitialized ||
            !::spinnerTipo.isInitialized ||
            !::spinnerEstado.isInitialized ||
            !::btnCambiarVista.isInitialized
        ) return

        val tipo = spinnerTipo.selectedItem?.toString()
            ?: "Todos los tipos"

        val estado = spinnerEstado.selectedItem?.toString()
            ?: "Todos los estados"

        val filtrados = todosLosReportes.filter { (_, reporte) ->
            (tipo == "Todos los tipos" ||
                    reporte.tipo.equals(tipo, true)) &&
                    (estado == "Todos los estados" ||
                            reporte.estado.equals(estado, true))
        }

        mostrarMapa(filtrados)
    }

    private fun mostrarMapa(
        datos: List<Pair<UbicacionPublica, ReportePublico>>
    ) {
        mapa.overlays.clear()

        if (modoCalor) {
            val puntos = datos.map { (ubicacion, _) ->
                GeoPoint(ubicacion.latitud, ubicacion.longitud)
            }

            mapa.overlays.add(MapaCalorOverlay(puntos))

            btnCambiarVista.text = "Ver marcadores"
            txtLeyenda.text =
                "Mapa de calor: rojo = mayor concentración"
            scrollLeyenda.visibility = View.GONE

        } else {
            for ((ubicacion, reporte) in datos) {
                val marcador = Marker(mapa)

                marcador.position = GeoPoint(
                    ubicacion.latitud,
                    ubicacion.longitud
                )

                marcador.icon =
                    MarcadorIncidenteFactory.crearMarcador(
                        this,
                        reporte.tipo
                    )

                marcador.title = reporte.tipo

                marcador.setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
                )

                marcador.setOnMarkerClickListener { _, _ ->
                    mostrarDetalle(reporte)
                    true
                }

                mapa.overlays.add(marcador)
            }

            btnCambiarVista.text = "Ver mapa de calor"
            txtLeyenda.text = "Categorías de incidentes"
            scrollLeyenda.visibility = View.VISIBLE
        }

        contador.text =
            "Mostrando ${datos.size} de ${todosLosReportes.size} incidentes"

        if (datos.isNotEmpty()) {
            val primera = datos.first().first

            mapa.controller.animateTo(
                GeoPoint(primera.latitud, primera.longitud)
            )
        }

        mapa.invalidate()
    }

    private fun mostrarDetalle(reporte: ReportePublico) {
        val vista = layoutInflater.inflate(
            R.layout.dialog_detalle_incidente,
            null
        )

        val txtTipo =
            vista.findViewById<TextView>(R.id.txtDetalleTipo)

        val txtEstado =
            vista.findViewById<TextView>(R.id.txtDetalleEstado)

        val txtDescripcion =
            vista.findViewById<TextView>(R.id.txtDetalleDescripcion)

        val txtFecha =
            vista.findViewById<TextView>(R.id.txtDetalleFecha)

        txtTipo.text = reporte.tipo
        txtTipo.setTextColor(
            MarcadorIncidenteFactory.obtenerColor(reporte.tipo)
        )

        txtEstado.text = reporte.estado

        when (reporte.estado.trim().lowercase(Locale.ROOT)) {
            "pendiente" -> {
                txtEstado.setTextColor(Color.parseColor("#92400E"))
                txtEstado.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#FEF3C7"))
            }
            "en proceso" -> {
                txtEstado.setTextColor(Color.parseColor("#1D4ED8"))
                txtEstado.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#DBEAFE"))
            }
            "resuelto" -> {
                txtEstado.setTextColor(Color.parseColor("#166534"))
                txtEstado.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#DCFCE7"))
            }
            else -> {
                txtEstado.setTextColor(Color.parseColor("#334155"))
                txtEstado.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#F1F5F9"))
            }
        }

        txtDescripcion.text = reporte.descripcion

        val formato = SimpleDateFormat(
            "dd/MM/yyyy - hh:mm a",
            Locale("es", "GT")
        )

        txtFecha.text = "Publicado: " +
                formato.format(Date(reporte.fechaCreacion))

        AlertDialog.Builder(this)
            .setView(vista)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        mapa.onResume()
    }

    override fun onPause() {
        mapa.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapa.onDetach()
        super.onDestroy()
    }
}