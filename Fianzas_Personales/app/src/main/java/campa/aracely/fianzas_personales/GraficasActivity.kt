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
    lateinit var botonGuardar: View

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
                        "@string/dick123" -> alimentos += i.monto
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
                val gastos = Gastos(nombre, porcentaje, color, monto, categorias, fecha)

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
        lista.add(Gastos("", pAlimentos, R.color.black, alimentos, "Alimentos", fecha))
        lista.add(Gastos("", pTransporte, R.color.azul1_1, transporte, "Transporte", fecha))
        lista.add(Gastos("", pCompras, R.color.white, compras, "Compras", fecha))
        lista.add(Gastos("", pFacturas, R.color.azul1_0, facturas, "Facturas", fecha))
        lista.add(Gastos("", pOtros, R.color.rojo_fuerte, otros, "Otros", fecha))

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

        for (i in lista) {
            when (i.categoria) {
                "Alimentos" -> alimentos += i.monto
                "Transporte" -> transporte += i.monto
                "Compras" -> compras += i.monto
                "Facturas" -> facturas += i.monto
                "Otros" -> otros += i.monto
            }
        }

        actualizarGrafica()
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
            j.put("mes", i.mes)
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
        val prueba: Button = findViewById(R.id.prueba)
        val spinner_mes: Spinner = findViewById(R.id.spinner_mes)
        val spinner_categoria: Spinner = findViewById(R.id.spinner_categoria)
        graph = findViewById(R.id.graph)

        botonGuardar = findViewById(R.id.guardar)

        val meses = listOf(
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
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                val selectedItem = meses[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se necesita implementar nada aquí
            }
        }

        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_categoria.adapter = adapter2
        spinner_categoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                val selectedItem = categorias[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se necesita implementar nada aquí
            }
        }

        fetchingData()
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

        botonGuardar.setOnClickListener {
            guardar()
            extraerDatos()
            montoAlimentos.text = "$alimentos"
            montoTransporte.text = "$transporte"
            montoCompras.text = "$compras"
            montoFacturas.text = "$facturas"
            montoOtros.text = "$otros"
        }

        prueba.setOnClickListener {
            lista.add(Gastos("", 5.0f, R.color.black, 5.0f, "Compras", LocalDate.now()))
            lista.add(Gastos("", 5.0f, R.color.black, 5.0f, "Facturas", LocalDate.now()))
            extraerDatos()
        }
    }
}
