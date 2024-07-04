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
import androidx.core.content.ContextCompat
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
        extraerDatos()
        actualizarTextViews(lista)
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
    fun extraerDatos() {
        // Limpia los totales antes de recalcular
        alimentos = 0.0F
        transporte = 0.0F
        compras = 0.0F
        facturas = 0.0F
        otros = 0.0F

        // Variables para sumar los ingresos
        var ingresosAlimentos = 0.0F
        var ingresosTransporte = 0.0F
        var ingresosCompras = 0.0F
        var ingresosFacturas = 0.0F
        var ingresosOtros = 0.0F

        // Recorre la lista y suma los montos correspondientes
        for (gasto in lista) {
            when (gasto.categoria) {
                "ALIMENTOS" -> {
                    when (gasto.tipoGasto) {
                        "Gasto" -> alimentos += -gasto.cantidad
                        "Ingreso" -> ingresosAlimentos += gasto.cantidad
                    }
                }
                "TRANSPORTE" -> {
                    when (gasto.tipoGasto) {
                        "Gasto" -> transporte += -gasto.cantidad
                        "Ingreso" -> ingresosTransporte += gasto.cantidad
                    }
                }
                "COMPRAS" -> {
                    when (gasto.tipoGasto) {
                        "Gasto" -> compras += -gasto.cantidad
                        "Ingreso" -> ingresosCompras += gasto.cantidad
                    }
                }
                "FACTURAS" -> {
                    when (gasto.tipoGasto) {
                        "Gasto" -> facturas += -gasto.cantidad
                        "Ingreso" -> ingresosFacturas += gasto.cantidad
                    }
                }
                "OTROS" -> {
                    when (gasto.tipoGasto) {
                        "Gasto" -> otros += -gasto.cantidad
                        "Ingreso" -> ingresosOtros += gasto.cantidad
                    }
                }
            }
        }

        // Restar los ingresos de los gastos correspondientes
        alimentos -= ingresosAlimentos
        transporte -= ingresosTransporte
        compras -= ingresosCompras
        facturas -= ingresosFacturas
        otros -= ingresosOtros

        // Actualizar la gráfica si hay datos, o mostrar el mensaje si no hay datos
        if (lista.isEmpty()) {
            // Mostrar el mensaje en lugar de la gráfica
            graph.visibility = View.GONE
            textoSinDatos.visibility = View.VISIBLE
        } else {
            // Actualizar los montos y la gráfica con los datos actuales
            actualizarGrafica()
            graph.visibility = View.VISIBLE
            textoSinDatos.visibility = View.GONE
        }
    }

    fun actualizarTextViews(listaFiltrada: List<Transaccion>) {
        var montoAlimentos = 0.0F
        var montoTransporte = 0.0F
        var montoCompras = 0.0F
        var montoFacturas = 0.0F
        var montoOtros = 0.0F

        // Sumar los montos según la categoría
        for (gasto in listaFiltrada) {
            when (gasto.categoria) {
                "ALIMENTOS" -> montoAlimentos += gasto.cantidad
                "TRANSPORTE" -> montoTransporte += gasto.cantidad
                "COMPRAS" -> montoCompras += gasto.cantidad
                "FACTURAS" -> montoFacturas += gasto.cantidad
                "OTROS" -> montoOtros += gasto.cantidad
            }
        }

        // Actualizar los TextViews con los nuevos montos
        val montoAlimentosTextView: TextView = findViewById(R.id.textoAlimentos)
        val montoTransporteTextView: TextView = findViewById(R.id.textoTransporte)
        val montoComprasTextView: TextView = findViewById(R.id.textoCompras)
        val montoFacturasTextView: TextView = findViewById(R.id.textoFacturas)
        val montoOtrosTextView: TextView = findViewById(R.id.textoOtros)

        montoAlimentosTextView.text = "$montoAlimentos"
        montoTransporteTextView.text = "$montoTransporte"
        montoComprasTextView.text = "$montoCompras"
        montoFacturasTextView.text = "$montoFacturas"
        montoOtrosTextView.text = "$montoOtros"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun actualizarGrafica() {
        val total = alimentos + transporte + compras + facturas + otros
        if (total == 0.0f) {
            Log.e("ActualizarGrafica", "El total es cero, no se pueden calcular porcentajes")
            textoSinDatos.visibility = View.VISIBLE
            graph.visibility = View.GONE
            return
        }

        textoSinDatos.visibility = View.GONE
        graph.visibility = View.VISIBLE

        val pAlimentos: Float = (alimentos * 100 / total).toFloat()
        val pTransporte: Float = (transporte * 100 / total).toFloat()
        val pCompras: Float = (compras * 100 / total).toFloat()
        val pFacturas: Float = (facturas * 100 / total).toFloat()
        val pOtros: Float = (otros * 100 / total).toFloat()

        val data = listOf(
            Triple(pAlimentos, ContextCompat.getColor(this, R.color.ColorAlimentos), "Alimentos"),
            Triple(pTransporte, ContextCompat.getColor(this, R.color.ColorTransporte), "Transporte"),
            Triple(pCompras, ContextCompat.getColor(this, R.color.ColorCompras), "Compras"),
            Triple(pFacturas, ContextCompat.getColor(this, R.color.ColorFacturas), "Facturas"),
            Triple(pOtros, ContextCompat.getColor(this, R.color.ColorOtros), "Otros")
        )

        val fondo = CustomCircleDrawable(this, data)
        graph.background = fondo
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
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val mesSeleccionado = meses[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val adapterCategoria = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_categoria.adapter = adapterCategoria
        spinner_categoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                filtrarCategoria()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
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
