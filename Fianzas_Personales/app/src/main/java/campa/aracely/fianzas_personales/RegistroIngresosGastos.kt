package campa.aracely.fianzas_personales

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.*

class RegistroIngresosGastos : AppCompatActivity() {

    private lateinit var edCantidad: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var actvCategoria: AutoCompleteTextView
    private lateinit var tipoIngresoGasto: AutoCompleteTextView
    private lateinit var btnRegistrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_ingresos_gastos)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etFechaNacimiento = findViewById(R.id.et_fecha_nacimiento)
        edCantidad = findViewById(R.id.ed_cantidad_ingreso_gasto)
        actvCategoria = findViewById(R.id.ed_categoria)
        tipoIngresoGasto = findViewById(R.id.ed_tipo_gasto)
        btnRegistrar = findViewById(R.id.btn_registrar_ingreso_gasto)

        activateCalendar()

        activateCategoriaDropdown()

        activateTipoIngresoGastoDropdown()


        btnRegistrar.setOnClickListener {
            if (camposValidos()) {
                Toast.makeText(this, getString(R.string.ingreso_gasto_registrado), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ActivityInicio::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun activateTipoIngresoGastoDropdown() {
        val tipos = listOf(getString(R.string.ingreso), getString(R.string.gasto))
        val adapterTipos = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tipos)
        tipoIngresoGasto.setAdapter(adapterTipos)
        tipoIngresoGasto.setOnClickListener {
            tipoIngresoGasto.showDropDown()
        }
    }

    private fun activateCategoriaDropdown() {
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
    }

    private fun activateCalendar() {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dateFormat = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(dateFormat, Locale.US)
            etFechaNacimiento.setText(sdf.format(calendar.time))
        }

        etFechaNacimiento.setOnClickListener {
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
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

    private fun camposValidos(): Boolean {
        val cantidadValida = edCantidad.text.isNotEmpty() && isValidDouble(edCantidad.text.toString())
        val fechaValida = etFechaNacimiento.text.isNotEmpty()
        val categoriaValida = actvCategoria.text.isNotEmpty()
        val tipoValido = tipoIngresoGasto.text.isNotEmpty()

        if (!cantidadValida) {
            edCantidad.error = "Ingrese una cantidad válida"
        }

        if (!fechaValida) {
            etFechaNacimiento.error = "La fecha de registro es obligatoria"
        }

        if (!categoriaValida) {
            actvCategoria.error = "La categoría es obligatoria"
        }

        if (!tipoValido) {
            tipoIngresoGasto.error = "El tipo de ingreso o gasto es obligatorio"
        }

        return cantidadValida && fechaValida && categoriaValida && tipoValido
    }
}
