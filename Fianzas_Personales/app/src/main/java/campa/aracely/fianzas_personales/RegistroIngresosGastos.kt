package campa.aracely.fianzas_personales

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.*

class RegistroIngresosGastos : AppCompatActivity() {

    private lateinit var edCantidad: EditText
    private lateinit var et_fecha_registro: EditText
    private lateinit var actvCategoria: AutoCompleteTextView
    private lateinit var tipoIngresoGasto: AutoCompleteTextView
    private lateinit var btnRegistrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_ingresos_gastos)

        configurarBordesVentana()
        inicializarVistas()
        configurarDatePicker()
        configurarDropdown(actvCategoria, obtenerCategorias())
        configurarDropdown(tipoIngresoGasto, obtenerTiposIngresoGasto())
        configurarBotonRegistrar()
    }

    private fun configurarBordesVentana() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun inicializarVistas() {
        et_fecha_registro = findViewById(R.id.et_fecha_nacimiento)
        edCantidad = findViewById(R.id.ed_cantidad_ingreso_gasto)
        actvCategoria = findViewById(R.id.ed_categoria)
        tipoIngresoGasto = findViewById(R.id.ed_tipo_gasto)
        btnRegistrar = findViewById(R.id.btn_registrar_ingreso_gasto)
    }

    private fun configurarDatePicker() {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dateFormat = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(dateFormat, Locale.US)
            et_fecha_registro.setText(sdf.format(calendar.time))
        }

        et_fecha_registro.setOnClickListener {
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun configurarDropdown(campo: AutoCompleteTextView, opciones: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opciones)
        campo.setAdapter(adapter)
        campo.setOnClickListener { campo.showDropDown() }
    }

    private fun obtenerCategorias(): List<String> {
        return listOf(
            getString(R.string.alimentos),
            getString(R.string.facturas),
            getString(R.string.compras),
            getString(R.string.transportes),
            getString(R.string.otros)
        )
    }

    private fun obtenerTiposIngresoGasto(): List<String> {
        return listOf(getString(R.string.ingreso), getString(R.string.gasto))
    }

    private fun configurarBotonRegistrar() {
        btnRegistrar.setOnClickListener {
            if (camposValidos()) {
                registrarIngresoGasto()
            } else {
                Toast.makeText(this, getString(R.string.mensaje_campos_incompletos), Toast.LENGTH_SHORT).show()
            }
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
        val fechaValida = et_fecha_registro.text.isNotEmpty()
        val categoriaValida = actvCategoria.text.isNotEmpty()
        val tipoValido = tipoIngresoGasto.text.isNotEmpty()

        if (!cantidadValida) edCantidad.error = getString(R.string.mensaje_validacion_cantidad)
        if (!fechaValida) et_fecha_registro.error = getString(R.string.mensaje_validacion_fecha)
        if (!categoriaValida) actvCategoria.error = getString(R.string.mensaje_validacion_categoria)
        if (!tipoValido) tipoIngresoGasto.error = getString(R.string.mensaje_validacion_tipo_gasto)

        return cantidadValida && fechaValida && categoriaValida && tipoValido
    }

    private fun registrarIngresoGasto() {
        val fecha = et_fecha_registro.text.toString()
        val categoria = actvCategoria.text.toString()
        val cantidad = edCantidad.text.toString().toDouble()
        val tipo = tipoIngresoGasto.text.toString()
        val intent = Intent(this, ActivityHistorial::class.java)
        startActivity(intent)
        finish()
    }
}
