package campa.aracely.fianzas_personales

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class ActivityHistorial : AppCompatActivity() {

    private lateinit var tvCantidad: TextView
    private lateinit var tvCategoria: TextView
    private lateinit var tvTipoGasto: TextView
    private lateinit var tvFecha: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
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
        val user = auth.currentUser
        val userId = user?.uid

        firestore.collection("ingresos_gastos")
            .whereEqualTo("usuario", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if(!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val cantidad = document.getString("cantidad") ?: ""
                    val categoria = document.getString("categoria") ?: ""
                    val tipoGasto = document.getString("tipo") ?: ""
                    val fecha = document.getString("fecha") ?: ""
                    val descripcion = document.getString("descripcion") ?: ""

                    tvCantidad.text = cantidad
                    tvCategoria.text = categoria
                    tvTipoGasto.text = tipoGasto
                    tvFecha.text = fecha
                    tvDescripcion.text = descripcion
                } else {
                    Toast.makeText(this, "No se encontraron datos para el usuario", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al obtener datos: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("FirestoreError", "Error al obtener los datos", exception)
            }
    }
}
