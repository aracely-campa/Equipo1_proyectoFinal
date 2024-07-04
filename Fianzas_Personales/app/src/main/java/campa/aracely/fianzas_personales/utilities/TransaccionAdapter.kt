package campa.aracely.fianzas_personales

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import campa.aracely.fianzas_personales.utilities.Transaccion

class TransaccionAdapter(private val context: Context, private val transacciones: List<Transaccion>) : BaseAdapter() {

    override fun getCount(): Int = transacciones.size

    override fun getItem(position: Int): Any = transacciones[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_transaccion, parent, false)
        } else {
            view = convertView
        }

        val transaccion = getItem(position) as Transaccion

        val tvCategoria = view.findViewById<TextView>(R.id.tv_categoria)
        val tvFecha = view.findViewById<TextView>(R.id.tv_fecha)
        val tvDescripcion = view.findViewById<TextView>(R.id.tv_descripcion)
        val tvTipo = view.findViewById<TextView>(R.id.tv_tipo)

        tvCategoria.text = transaccion.categoria
        tvFecha.text = transaccion.fecha
        tvDescripcion.text = transaccion.descripcion
        tvTipo.text = transaccion.tipo

        return view
    }
}
