package com.helpme.redciudadana.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.helpme.redciudadana.R
import com.helpme.redciudadana.model.ReportePublico
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportePublicoAdapter :
    RecyclerView.Adapter<ReportePublicoAdapter.ViewHolder>() {

    private val lista = mutableListOf<ReportePublico>()

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val tipo: TextView = view.findViewById(R.id.txtTipoPublico)
        val descripcion: TextView =
            view.findViewById(R.id.txtDescripcionPublico)
        val estado: TextView = view.findViewById(R.id.txtEstadoPublico)
        val fecha: TextView = view.findViewById(R.id.txtFechaPublico)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reporte_publico, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val reporte = lista[position]

        holder.tipo.text = reporte.tipo
        holder.descripcion.text = reporte.descripcion
        holder.estado.text = "Estado: ${reporte.estado}"

        val formato = SimpleDateFormat(
            "dd/MM/yyyy - hh:mm a",
            Locale("es", "GT")
        )

        holder.fecha.text =
            if (reporte.fechaCreacion > 0L) {
                formato.format(Date(reporte.fechaCreacion))
            } else {
                "Fecha no disponible"
            }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizar(nuevos: List<ReportePublico>) {
        lista.clear()
        lista.addAll(nuevos)
        notifyDataSetChanged()
    }
}