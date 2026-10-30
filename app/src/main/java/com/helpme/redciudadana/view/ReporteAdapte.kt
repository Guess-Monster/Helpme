package com.helpme.redciudadana.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.helpme.redciudadana.R
import com.helpme.redciudadana.model.Reporte
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReporteAdapter(
    private val reportes: MutableList<Reporte>
) : RecyclerView.Adapter<ReporteAdapter.ViewHolder>() {

    class ViewHolder(vista: View) :
        RecyclerView.ViewHolder(vista) {

        val tipo: TextView =
            vista.findViewById(R.id.txtTipoReporte)

        val descripcion: TextView =
            vista.findViewById(R.id.txtDescripcionReporte)

        val estado: TextView =
            vista.findViewById(R.id.txtEstadoReporte)

        val fecha: TextView =
            vista.findViewById(R.id.txtFechaReporte)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reporte, parent, false)

        return ViewHolder(vista)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val reporte = reportes[position]

        holder.tipo.text = reporte.tipo
        holder.descripcion.text = reporte.descripcion
        holder.estado.text = "Estado: ${reporte.estado}"

        val formato = SimpleDateFormat(
            "dd/MM/yyyy - hh:mm a",
            Locale("es", "GT")
        )

        holder.fecha.text =
            if (reporte.fechaCreacion > 0) {
                formato.format(Date(reporte.fechaCreacion))
            } else {
                "Fecha no disponible"
            }

        val color = when (reporte.estado) {
            "Pendiente" -> "#B45309"
            "En proceso" -> "#2563EB"
            "Resuelto" -> "#15803D"
            else -> "#64748B"
        }

        holder.estado.setTextColor(Color.parseColor(color))
    }

    override fun getItemCount() = reportes.size

    fun actualizarReportes(nuevos: List<Reporte>) {
        reportes.clear()
        reportes.addAll(nuevos)
        notifyDataSetChanged()
    }
}