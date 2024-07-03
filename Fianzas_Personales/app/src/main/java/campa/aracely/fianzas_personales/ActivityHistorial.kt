package campa.aracely.fianzas_personales

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ActivityHistorial : AppCompatActivity() {

    private lateinit var tvCantidad: TextView
    private lateinit var tvCategoria: TextView
    private lateinit var tvTipoGasto: TextView
    private lateinit var tvFecha: TextView
    private lateinit var tvDescripcion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        inicializarVistas()
        mostrarDatos()
    }

    private fun inicializarVistas() {
        tvCantidad = findViewById(R.id.tv_cantidad)
        tvCategoria = findViewById(R.id.tv_categoria)
        tvTipoGasto = findViewById(R.id.tv_tipo_gasto)
        tvFecha = findViewById(R.id.tv_fecha)
        tvDescripcion = findViewById(R.id.tv_descripcion)
    }

    private fun mostrarDatos() {
        val cantidad = intent.getStringExtra("cantidad") ?: ""
        val categoria = intent.getStringExtra("categoria") ?: ""
        val tipoGasto = intent.getStringExtra("tipoGasto") ?: ""
        val fecha = intent.getStringExtra("fecha") ?: ""
        val descripcion = intent.getStringExtra("descripcion") ?: ""

        tvCantidad.text = cantidad
        tvCategoria.text = categoria
        tvTipoGasto.text = tipoGasto
        tvFecha.text = fecha
        tvDescripcion.text = descripcion
    }
}
