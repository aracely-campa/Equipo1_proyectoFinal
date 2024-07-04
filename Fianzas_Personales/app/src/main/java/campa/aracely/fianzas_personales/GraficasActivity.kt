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

class GraficasActivity : AppCompatActivity() {
    private var lista = mutableListOf<Transaccion>()
    private var alimentos = 0.0F
    private var transporte = 0.0F
    private var compras = 0.0F
    private var facturas = 0.0F
    private var otros = 0.0F
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var graph: View
    private lateinit var spinner_categoria: Spinner
    private lateinit var spinner_mes: Spinner
    private lateinit var textoSinDatos: View

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


        configurarSpinners()
        obtenerDatosDeFirestore()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun obtenerDatosDeFirestore() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .collection("transacciones")
                .get()
                .addOnSuccessListener { documents ->
                    lista.clear()
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
        // Limpia los totales antes de recalcular
        alimentos = 0.0F
        transporte = 0.0F
        compras = 0.0F
        facturas = 0.0F
        otros = 0.0F

        // Variables para sumar los ingresos y gastos por categoría
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
                        "Gasto" -> alimentos += gasto.cantidad
                        "Ingreso" -> ingresosAlimentos += gasto.cantidad
                    }
                }
                "TRANSPORTE" -> {
                    when (gasto.tipoGasto) {
                        "Gasto" -> transporte += gasto.cantidad
                        "Ingreso" -> ingresosTransporte += gasto.cantidad
                    }
                }
                "COMPRAS" -> {
                    when (gasto.tipoGasto) {
                        "Gasto" -> compras += gasto.cantidad
                        "Ingreso" -> ingresosCompras += gasto.cantidad
                    }
                }
                "FACTURAS" -> {
                    when (gasto.tipoGasto) {
                        "Gasto" -> facturas += gasto.cantidad
                        "Ingreso" -> ingresosFacturas += gasto.cantidad
                    }
                }
                "OTROS" -> {
                    when (gasto.tipoGasto) {
                        "Gasto" -> otros += gasto.cantidad
                        "Ingreso" -> ingresosOtros += gasto.cantidad
                    }
                }
            }
        }

        // Restar los ingresos de los gastos correspondientes
        alimentos = ingresosAlimentos - alimentos
        transporte = ingresosTransporte - transporte
        compras = ingresosCompras - compras
        facturas = ingresosFacturas - facturas
        otros = ingresosOtros - otros

        // Actualizar la gráfica si hay datos, o mostrar el mensaje si no hay datos
        if (lista.isEmpty()) {
            // Mostrar el mensaje en lugar de la gráfica
            graph.visibility = View.GONE
            textoSinDatos.visibility = View.VISIBLE
        } else {

            actualizarGrafica()
            graph.visibility = View.VISIBLE
            textoSinDatos.visibility = View.GONE
        }

        actualizarTextViews()
    }

    private fun actualizarTextViews() {
        // Actualizar los TextViews con los montos actuales
        val montoAlimentosTextView: TextView = findViewById(R.id.textoAlimentos)
        val montoTransporteTextView: TextView = findViewById(R.id.textoTransporte)
        val montoComprasTextView: TextView = findViewById(R.id.textoCompras)
        val montoFacturasTextView: TextView = findViewById(R.id.textoFacturas)
        val montoOtrosTextView: TextView = findViewById(R.id.textoOtros)

        montoAlimentosTextView.text = String.format("%.2f", alimentos)
        montoTransporteTextView.text = String.format("%.2f", transporte)
        montoComprasTextView.text = String.format("%.2f", compras)
        montoFacturasTextView.text = String.format("%.2f", facturas)
        montoOtrosTextView.text = String.format("%.2f", otros)
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
        // Configurar los spinners (meses y categorías)
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

        // Adaptadores para los spinners
        val adapterMes = ArrayAdapter(this, android.R.layout.simple_spinner_item, meses)
        adapterMes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_mes.adapter = adapterMes
        spinner_mes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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
    private fun filtrarCategoria() {
        val categoriaSeleccionada = spinner_categoria.selectedItem.toString().toUpperCase()
        val listaFiltrada = if (categoriaSeleccionada == "---") {
            lista
        } else {
            lista.filter { it.categoria.equals(categoriaSeleccionada, ignoreCase = true) }  // Filtrar por categoría
        }

        // Actualizar gráfica y textos con los datos filtrados
        extraerDatosFiltrados(listaFiltrada)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun extraerDatosFiltrados(listaFiltrada: List<Transaccion>) {
        // Limpia los totales antes de recalcular
        alimentos = 0.0F
        transporte = 0.0F
        compras = 0.0F
        facturas = 0.0F
        otros = 0.0F

        // Variables para sumar los ingresos y gastos por categoría
        var ingresosAlimentos = 0.0F
        var ingresosTransporte = 0.0F
        var ingresosCompras = 0.0F
        var ingresosFacturas = 0.0F
        var ingresosOtros = 0.0F

        // Recorre la lista filtrada y suma los montos correspondientes
        for (gasto in listaFiltrada) {
            when (gasto.categoria) {
                "ALIMENTOS" -> {
                    when (gasto.tipoGasto) {
                        "Gasto" -> alimentos += gasto.cantidad
                        "Ingreso" -> ingresosAlimentos += gasto.cantidad
                    }
                }
                "TRANSPORTE" -> {
                    when (gasto.tipoGasto) {
                        "Gasto" -> transporte += gasto.cantidad
                        "Ingreso" -> ingresosTransporte += gasto.cantidad
                    }
                }
                "COMPRAS" -> {
                    when (gasto.tipoGasto) {
                        "Gasto" -> compras += gasto.cantidad
                        "Ingreso" -> ingresosCompras += gasto.cantidad
                    }
                }
                "FACTURAS" -> {
                    when (gasto.tipoGasto) {
                        "Gasto" -> facturas += gasto.cantidad
                        "Ingreso" -> ingresosFacturas += gasto.cantidad
                    }
                }
                "OTROS" -> {
                    when (gasto.tipoGasto) {
                        "Gasto" -> otros += gasto.cantidad
                        "Ingreso" -> ingresosOtros += gasto.cantidad
                    }
                }
            }
        }

        // Restar los ingresos de los gastos correspondientes
        alimentos = ingresosAlimentos - alimentos
        transporte = ingresosTransporte - transporte
        compras = ingresosCompras - compras
        facturas = ingresosFacturas - facturas
        otros = ingresosOtros - otros

        if (listaFiltrada.isEmpty()) {

            graph.visibility = View.GONE
            textoSinDatos.visibility = View.VISIBLE
        } else {
            actualizarGrafica()
            graph.visibility = View.VISIBLE
            textoSinDatos.visibility = View.GONE
        }

        actualizarTextViews()
    }

    companion object {

        val listaCategorias = listOf("ALIMENTOS", "TRANSPORTE", "COMPRAS", "FACTURAS", "OTROS")
    }
}
