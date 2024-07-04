package campa.aracely.fianzas_personales

import CustomCircleDrawable
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import campa.aracely.fianzas_personales.utilities.Gastos
import campa.aracely.fianzas_personales.utilities.JSONFile
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter

class GraficasActivity : AppCompatActivity() {
    var jsonFile: JSONFile? = null
    var lista = ArrayList<Gastos>()
    var alimentos = 0.0F
    var transporte = 0.0F
    var compras = 0.0F
    var facturas = 0.0F
    var otros = 0.0F
    var data: Boolean = false

    lateinit var graph: View
    lateinit var graphAlimentos: View
    lateinit var graphTransporte: View
    lateinit var graphCompras: View
    lateinit var graphFacturas: View
    lateinit var graphOtros: View
    lateinit var spinner_categoria: Spinner
    lateinit var spinner_mes: Spinner
    lateinit var textoSinDatos: View

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchingData() {
        try {
            val json: String = jsonFile?.getData(context = this) ?: ""
            if (json != "") {
                this.data = true
                val jsonArray: JSONArray = JSONArray(json)
                this.lista = parseJson(jsonArray)
                for (i in lista) {
                    when (i.categoria) {
                        "Alimentos" -> alimentos += i.monto
                        "Transporte" -> transporte += i.monto
                        "Compras" -> compras += i.monto
                        "Facturas" -> facturas += i.monto
                        "otros" -> otros += i.monto
                    }
                }
            } else {
                this.data = false
            }
        } catch (exception: JSONException) {
            exception.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun parseJson(jsonArray: JSONArray): ArrayList<Gastos> {
        val lista = ArrayList<Gastos>()
        val formatter = DateTimeFormatter.ofPattern("MM-dd")
        val currentYear = LocalDate.now().year
        for (i in 0 until jsonArray.length()) {
            try {
                val nombre = jsonArray.getJSONObject(i).getString("nombre")
                val porcentaje = jsonArray.getJSONObject(i).getDouble("porcentaje").toFloat()
                val color = jsonArray.getJSONObject(i).getInt("color")
                val monto = jsonArray.getJSONObject(i).getDouble("monto").toFloat()
                val categorias = jsonArray.getJSONObject(i).getString("categorias")
                val fechaString = jsonArray.getJSONObject(i).getString("fecha")
                val fecha = LocalDate.parse(
                    "$currentYear-$fechaString",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                )
                val tipo = jsonArray.getJSONObject(i).getString("tipo")
                val gastos = Gastos(nombre, porcentaje, color, monto, categorias, fecha, tipo)

                lista.add(gastos)
            } catch (exception: JSONException) {
                exception.printStackTrace()
            }
        }
        return lista
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun actualizarGrafica() {
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

        Log.d("porcentajes", "alimentos $pAlimentos")
        Log.d("porcentajes", "transporte $pTransporte")
        Log.d("porcentajes", "compras $pCompras")
        Log.d("porcentajes", "facturas $pFacturas")
        Log.d("porcentajes", "otros $pOtros")

        lista.clear()
        lista.add(Gastos("", pAlimentos, R.color.ColorAlimentos, alimentos, "Alimentos", fecha, "Ingreso"))
        lista.add(Gastos("", pTransporte, R.color.ColorTransporte, transporte, "Transporte", fecha,"Ingreso"))
        lista.add(Gastos("", pCompras, R.color.ColorCompras, compras, "Compras", fecha,"Ingreso"))
        lista.add(Gastos("", pFacturas, R.color.ColorFacturas, facturas, "Facturas", fecha,"Ingreso"))
        lista.add(Gastos("", pOtros, R.color.ColorOtros, otros, "Otros", fecha,"Ingreso"))

        val fondo = CustomCircleDrawable(this, lista)
        graph.background = fondo
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun extraerDatos() {
        alimentos = 0.0F
        transporte = 0.0F
        compras = 0.0F
        facturas = 0.0F
        otros = 0.0F

        var ingresosAlimentos = 0.0F
        var ingresosTransporte = 0.0F
        var ingresosCompras = 0.0F
        var ingresosFacturas = 0.0F
        var ingresosOtros = 0.0F

        for (gasto in lista) {
            when (gasto.categoria) {
                "Alimentos" -> {
                    when (gasto.tipo) {
                        "Gasto" -> alimentos += gasto.monto
                        "Ingreso" -> ingresosAlimentos += gasto.monto
                    }
                }
                "Transporte" -> {
                    when (gasto.tipo) {
                        "Gasto" -> transporte += gasto.monto
                        "Ingreso" -> ingresosTransporte += gasto.monto
                    }
                }
                "Compras" -> {
                    when (gasto.tipo) {
                        "Gasto" -> compras += gasto.monto
                        "Ingreso" -> ingresosCompras += gasto.monto
                    }
                }
                "Facturas" -> {
                    when (gasto.tipo) {
                        "Gasto" -> facturas += gasto.monto
                        "Ingreso" -> ingresosFacturas += gasto.monto
                    }
                }
                "Otros" -> {
                    when (gasto.tipo) {
                        "Gasto" -> otros += gasto.monto
                        "Ingreso" -> ingresosOtros += gasto.monto
                    }
                }
            }
        }

        // Restar los gastos de los ingresos
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


    fun actualizarTextViews(listaFiltrada: List<Gastos>) {
        var montoAlimentos = 0.0F
        var montoTransporte = 0.0F
        var montoCompras = 0.0F
        var montoFacturas = 0.0F
        var montoOtros = 0.0F

        for (gasto in listaFiltrada) {
            when (gasto.categoria) {
                "Alimentos" -> montoAlimentos += gasto.monto
                "Transporte" -> montoTransporte += gasto.monto
                "Compras" -> montoCompras += gasto.monto
                "Facturas" -> montoFacturas += gasto.monto
                "Otros" -> montoOtros += gasto.monto
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
    fun filtrarPorMes(mesSeleccionado: String) {
        // Convertir el nombre del mes a su número correspondiente (1 para enero, 2 para febrero, etc.)
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
            else -> 0 // En caso de selección inválida o ---
        }

        // Filtrar la lista por el mes seleccionado
        val listaFiltrada = lista.filter { it.fecha.monthValue == numeroMes }

        // Actualizar la gráfica y los TextViews con los nuevos datos filtrados por mes
        actualizarTextViews(listaFiltrada)
        asignarColor()  // Asigna colores a los elementos filtrados

        val fondo = CustomCircleDrawable(this, ArrayList(listaFiltrada))
        graph.background = fondo
    }

    fun filtrarCategoria() {
        val categoriaSeleccionada =
            spinner_categoria.selectedItem.toString().toLowerCase().capitalize()

        val listaFiltrada = if (categoriaSeleccionada == "---") {
            lista  // Mostrar todos los elementos
        } else {
            lista.filter { it.categoria == categoriaSeleccionada }  // Filtrar por categoría

        }
        actualizarTextViews(listaFiltrada)
        asignarColor()  // Asigna colores a los elementos filtrados

        val fondo = CustomCircleDrawable(this, ArrayList(listaFiltrada))
        graph.background = fondo
    }

    fun asignarColor() {
        for (i in lista) {
            when (i.categoria) {
                "Alimentos" -> i.color = R.color.ColorAlimentos
                "Transporte" -> i.color = R.color.ColorTransporte
                "Compras" -> i.color = R.color.ColorCompras
                "Facturas" -> i.color = R.color.ColorFacturas
                "Otros" -> i.color = R.color.ColorOtros
            }
        }
    }


    fun guardar() {
        val jsonArray = JSONArray()
        var o = 0
        for (i in lista) {
            Log.d("objetos", i.toString())
            val j = JSONObject()
            j.put("nombre", i.nombre)
            j.put("porcentaje", i.porcentaje)
            j.put("color", i.color)
            j.put("monto", i.monto)
            j.put("categoria", i.categoria)
            j.put("mes", i.fecha)
            jsonArray.put(o, j)
            o++
        }
        jsonFile?.saveData(this, jsonArray.toString())
        Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_graficas)

        val montoAlimentos: TextView = findViewById(R.id.textoAlimentos)
        val montoTransporte: TextView = findViewById(R.id.textoTransporte)
        val montoCompras: TextView = findViewById(R.id.textoCompras)
        val montoFacturas: TextView = findViewById(R.id.textoFacturas)
        val montoOtros: TextView = findViewById(R.id.textoOtros)
        val spinner_mes: Spinner = findViewById(R.id.spinner_mes)
        textoSinDatos = findViewById(R.id.textoSinDatos)
        spinner_categoria = findViewById(R.id.spinner_categoria)

        graph = findViewById(R.id.graph)



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

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, meses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_mes.adapter = adapter
        spinner_mes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val mesSeleccionado = meses[position]
                filtrarPorMes(mesSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_categoria.adapter = adapter2
        spinner_categoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                filtrarCategoria()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        lista.add(Gastos("", 5.0f, R.color.azul_claro, 5.0f, "Compras", LocalDate.now(),"Ingreso"))
        lista.add(Gastos("", 5.0f, R.color.black, 5.0f, "Facturas", LocalDate.now(),"Ingreso"))

        lista.add(Gastos("", 5.0f, R.color.azul_claro, 5.0f, "Alimentos", LocalDate.now(),"Ingreso"))
        lista.add(Gastos("", 5.0f, R.color.azul_claro, 1.0f, "Alimentos", LocalDate.now(),"Gasto"))

        lista.add(Gastos("", 5.0f, R.color.black, 5.0f, "Transporte", LocalDate.now(),"Ingreso"))
        lista.add(Gastos("", 5.0f, R.color.black, 5.0f, "Otros", LocalDate.now(),"Ingreso"))

        actualizarGrafica()
        extraerDatos()
        fetchingData()
        filtrarCategoria()
        asignarColor()
        if (data) {
            extraerDatos()
            montoAlimentos.text = "$alimentos"
            montoTransporte.text = "$transporte"
            montoCompras.text = "$compras"
            montoFacturas.text = "$facturas"
            montoOtros.text = "$otros"

        } else {
            extraerDatos()
            montoAlimentos.text = "$alimentos"
            montoTransporte.text = "$transporte"
            montoCompras.text = "$compras"
            montoFacturas.text = "$facturas"
            montoOtros.text = "$otros"

        }


    }
}
