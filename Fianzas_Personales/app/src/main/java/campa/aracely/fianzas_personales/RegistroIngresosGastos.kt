package campa.aracely.fianzas_personales

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.*

class RegistroIngresosGastos : AppCompatActivity() {

    private lateinit var edCantidad: EditText
    private lateinit var etFechaRegistro: EditText
    private lateinit var actvCategoria: AutoCompleteTextView
    private lateinit var tipoIngresoGasto: AutoCompleteTextView
    private lateinit var btnRegistrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_ingresos_gastos)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

     
        edCantidad = findViewById(R.id.ed_cantidad_ingreso_gasto)
        etFechaRegistro = findViewById(R.id.ed_fecha_ingreso_gasto)
        actvCategoria = findViewById(R.id.ed_categoria)
        tipoIngresoGasto = findViewById(R.id.ed_tipo_gasto)
        btnRegistrar = findViewById(R.id.btn_registrar_ingreso_gasto)

        edCantidad.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!isValidDouble(s.toString())) {
                    edCantidad.error = getString(R.string.ingresa_numero_valido)
                } else {
                    edCantidad.error = null
                }
            }
        })

        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView(calendar)
        }

        etFechaRegistro.setOnClickListener {
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val categorias = listOf(
            getString(R.string.alimentos),
            getString(R.string.facturas),
            getString(R.string.compras),
            getString(R.string.transportes),
            getString(R.string.otros)
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categorias)
        actvCategoria.setAdapter(adapter)
        actvCategoria.setOnClickListener {
            actvCategoria.showDropDown()
        }

        val tipos = listOf(getString(R.string.ingreso), getString(R.string.gasto))
        val adapterTipos = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tipos)
        tipoIngresoGasto.setAdapter(adapterTipos)
        tipoIngresoGasto.setOnClickListener {
            tipoIngresoGasto.showDropDown()
        }

        btnRegistrar.setOnClickListener {

            Toast.makeText(this, getString(R.string.ingreso_gasto_registrado), Toast.LENGTH_SHORT).show()
        }
    }
    private fun isValidDouble(text: String): Boolean {
        return try {
            text.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun updateDateInView(calendar: Calendar) {
        val dateFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.US)
        etFechaRegistro.setText(sdf.format(calendar.time))
    }
}
