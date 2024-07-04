package campa.aracely.fianzas_personales

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class RegistroIngresosGastos : AppCompatActivity() {

    private lateinit var edCantidad: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var actvCategoria: AutoCompleteTextView
    private lateinit var tipoIngresoGasto: AutoCompleteTextView
    private lateinit var descripcion: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

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
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    private fun configurarBordesVentana() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun inicializarVistas() {
        etFechaNacimiento = findViewById(R.id.et_fecha_ingreso)
        edCantidad = findViewById(R.id.ed_cantidad_ingreso_gasto)
        actvCategoria = findViewById(R.id.ed_categoria)
        tipoIngresoGasto = findViewById(R.id.ed_tipo_gasto)
        descripcion = findViewById(R.id.ed_descripcion_ingreso)
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
                val cantidad = edCantidad.text.toString().toDouble()
                val fecha = etFechaNacimiento.text.toString()
                val categoria = actvCategoria.text.toString()
                val tipo = tipoIngresoGasto.text.toString()
                val descripcionText = descripcion.text.toString()

                val registro = hashMapOf(
                    "cantidad" to cantidad,
                    "fecha" to fecha,
                    "categoria" to categoria,
                    "tipoGasto" to tipo,
                    "descripcion" to descripcionText
                )

                val userId = auth.currentUser?.uid

                if (userId != null) {
                    firestore.collection("users").document(userId)
                        .collection("transacciones")
                        .add(registro)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this, getString(R.string.ingreso_gasto_registrado), Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, ActivityInicio::class.java))
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Error al registrar: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Error al obtener el ID del usuario", Toast.LENGTH_SHORT).show()
                }
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
        val fechaValida = etFechaNacimiento.text.isNotEmpty()
        val categoriaValida = actvCategoria.text.isNotEmpty()
        val tipoGastoValido = tipoIngresoGasto.text.isNotEmpty()
        val descripcionValida = descripcion.text.isNotEmpty()

        if (!cantidadValida) edCantidad.error = getString(R.string.mensaje_validacion_cantidad)
        if (!fechaValida) etFechaNacimiento.error = getString(R.string.mensaje_validacion_fecha)
        if (!categoriaValida) actvCategoria.error = getString(R.string.mensaje_validacion_categoria)
        if (!tipoGastoValido) tipoIngresoGasto.error = getString(R.string.mensaje_validacion_tipo_gasto)
        if (!descripcionValida) descripcion.error = getString(R.string.mensaje_validacion_descripcion)

        return cantidadValida && fechaValida && categoriaValida && tipoGastoValido && descripcionValida
    }
}
