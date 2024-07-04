package campa.aracely.fianzas_personales

import CustomCircleDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import campa.aracely.fianzas_personales.utilities.Transaccion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GraficasActivity : AppCompatActivity() {
    private var lista = mutableListOf<Transaccion>()
    var alimentos = 0.0F
    var transporte = 0.0F
    var compras = 0.0F
    var facturas = 0.0F
    var otros = 0.0F
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    lateinit var graph: View
    lateinit var spinner_categoria: Spinner
    lateinit var spinner_mes: Spinner
    lateinit var textoSinDatos: View

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_graficas)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val montoAlimentos: TextView = findViewById(R.id.textoAlimentos)
        val montoTransporte: TextView = findViewById(R.id.textoTransporte)
        val montoCompras: TextView = findViewById(R.id.textoCompras)
        val montoFacturas: TextView = findViewById(R.id.textoFacturas)
        val montoOtros: TextView = findViewById(R.id.textoOtros)
        textoSinDatos = findViewById(R.id.textoSinDatos)
        spinner_categoria = findViewById(R.id.spinner_categoria)
        spinner_mes = findViewById(R.id.spinner_mes)
        graph = findViewById(R.id.graph)

        // Configurar spinners (meses y categorías)
        configurarSpinners()

        // Obtener datos de Firestore
        obtenerDatosDeFirestore()

        // Actualizar gráfica inicial
        actualizarGrafica()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun obtenerDatosDeFirestore() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .collection("transacciones")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val transaccion = document.toObject(Transaccion::class.java)
                         transaccion.id = document.id
                        lista.add(transaccion)
                    }
                    // Extraer datos y actualizar gráfica
                    extraerDatos()
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error getting documents: ", exception)
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun extraerDatos() {
        alimentos = 0.0F
        transporte = 0.0F
        compras = 0.0F
        facturas = 0.0F
        otros = 0.0F

        for (gasto in lista) {
            when (gasto.categoria) {
                "Alimentos" -> alimentos += gasto.cantidad.toFloat()
                "Transporte" -> transporte += gasto.cantidad.toFloat()
                "Compras" -> compras +=gasto.cantidad.toFloat()
                "Facturas" -> facturas += gasto.cantidad.toFloat()
                "Otros" -> otros += gasto.cantidad.toFloat()
            }
        }

        actualizarGrafica()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun actualizarGrafica() {
        val total = alimentos + transporte + compras + facturas + otros
        if (total == 0.0f) {
            Log.e("ActualizarGrafica", "El total es cero, no se pueden calcular porcentajes")
            return
        }

        val pAlimentos: Float = (alimentos * 100 / total).toFloat()
        val pTransporte: Float = (transporte * 100 / total).toFloat()
        val pCompras: Float = (compras * 100 / total).toFloat()
        val pFacturas: Float = (facturas * 100 / total).toFloat()
        val pOtros: Float = (otros * 100 / total).toFloat()

        val currentYear = LocalDate.now().year
        val fechaString = "$currentYear-01-01"
        val fecha = LocalDate.parse(fechaString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        lista.clear()


        val fondo = CustomCircleDrawable(this, lista)
        graph.background = fondo
    }

    fun obtenerColor(){


    }

    private fun configurarSpinners() {
        val meses = listOf(
            "---",
            getString(R.string.enero),
            getString(R.string.febrero),
            getString(R.string.marzo),
            getString(R.string.abril),
            getString(R.string.mayo),
            getString(R.string.junio),
            getString(R.string.julio),
            getString(R.string.agosto),
            getString(R.string.septiembre),
            getString(R.string.octubre),
            getString(R.string.noviembre),
            getString(R.string.diciembre)
        )
        val categorias = listOf(
            "---",
            getString(R.string.alimentos),
            getString(R.string.compras),
            getString(R.string.transportes),
            getString(R.string.facturas),
            getString(R.string.otros)
        )

        val adapterMes = ArrayAdapter(this, android.R.layout.simple_spinner_item, meses)
        adapterMes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_mes.adapter = adapterMes
        spinner_mes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val mesSeleccionado = meses[position]
                filtrarPorMes(mesSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val adapterCategoria = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_categoria.adapter = adapterCategoria
        spinner_categoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                filtrarCategoria()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun filtrarPorMes(mesSeleccionado: String) {
        val numeroMes = when (spinner_mes.selectedItem) {
            getString(R.string.enero) -> 1
            getString(R.string.febrero) -> 2
            getString(R.string.marzo) -> 3
            getString(R.string.abril) -> 4
            getString(R.string.mayo) -> 5
            getString(R.string.junio) -> 6
            getString(R.string.julio) -> 7
            getString(R.string.agosto) -> 8
            getString(R.string.septiembre) -> 9
            getString(R.string.octubre) -> 10
            getString(R.string.noviembre) -> 11
            getString(R.string.diciembre) -> 12
            else -> 0
        }

        val listaFiltrada = lista.filter {
            it.fecha.monthValue == numeroMes
        }

        lista.clear()
        lista.addAll(listaFiltrada)

        actualizarGrafica()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun filtrarCategoria() {
        val categoriaSeleccionada = spinner_categoria.selectedItem.toString()
        if (categoriaSeleccionada == "---") {
            obtenerDatosDeFirestore()
            return
        }

        val listaFiltrada = lista.filter {
            it.categoria == categoriaSeleccionada
        }

        lista.clear()
        lista.addAll(listaFiltrada)

        actualizarGrafica()
    }
}
