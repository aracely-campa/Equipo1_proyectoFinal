package campa.aracely.fianzas_personales.Classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import campa.aracely.fianzas_personales.R

class TransaccionAdapter(
    private val transacciones: List<Transaccion>,
    private val onDeleteClick: (Transaccion) -> Unit
) : RecyclerView.Adapter<TransaccionAdapter.TransaccionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaccionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaccion, parent, false)
        return TransaccionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransaccionViewHolder, position: Int) {
        val transaccion = transacciones[position]
        holder.bind(transaccion)
        holder.itemView.findViewById<View>(R.id.btn_delete).setOnClickListener {
            onDeleteClick(transaccion)
        }
    }

    override fun getItemCount(): Int = transacciones.size

    class TransaccionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoria: TextView = itemView.findViewById(R.id.tv_categoria)
        private val cantidad: TextView = itemView.findViewById(R.id.tv_cantidad)
        private val descripcion: TextView = itemView.findViewById(R.id.tv_descripcion)
        private val fecha: TextView = itemView.findViewById(R.id.tv_fecha)
        private val tipoGasto: TextView = itemView.findViewById(R.id.tv_tipo_gasto)

        fun bind(transaccion: Transaccion) {
            categoria.text = transaccion.categoria
            cantidad.text = transaccion.cantidad.toString()
            descripcion.text = transaccion.descripcion
            fecha.text = transaccion.fecha
            tipoGasto.text = transaccion.tipoGasto
        }
    }
}
