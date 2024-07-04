package campa.aracely.fianzas_personales.utilities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import campa.aracely.fianzas_personales.R

class TransaccionAdapter(private val transacciones: List<Transaccion>, private val eliminarListener: (Transaccion) -> Unit) : RecyclerView.Adapter<TransaccionAdapter.TransaccionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaccionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaccion, parent, false)
        return TransaccionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransaccionViewHolder, position: Int) {
         holder.bind(transacciones[position])
    }

    override fun getItemCount(): Int {
        return transacciones.size
    }

    inner class TransaccionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategoria: TextView = itemView.findViewById(R.id.tv_categoria)
        private val tvCantidad: TextView = itemView.findViewById(R.id.tv_cantidad)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tv_descripcion)
        private val tvFecha: TextView = itemView.findViewById(R.id.tv_fecha)
        private val tvTipoGasto: TextView = itemView.findViewById(R.id.tv_tipo_gasto)
        private val btnEliminar: Button = itemView.findViewById(R.id.btn_eliminar)

        fun bind(transaccion: Transaccion) {
            tvCategoria.text = transaccion.categoria
            tvCantidad.text = transaccion.cantidad.toString()
            tvDescripcion.text = transaccion.descripcion
            tvFecha.text = transaccion.fecha
            tvTipoGasto.text = transaccion.tipoGasto

            btnEliminar.setOnClickListener {
                eliminarListener(transaccion)
            }
        }
    }
}