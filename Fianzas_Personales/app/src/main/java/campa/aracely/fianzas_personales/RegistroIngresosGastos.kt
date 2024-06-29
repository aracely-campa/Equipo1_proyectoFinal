package campa.aracely.fianzas_personales

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Locale

class RegistroIngresosGastos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_ingresos_gastos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /** Inicio edCantidad */
        // Inicializa el edCantidad
        val edCantidad: EditText = findViewById(R.id.ed_cantidad_ingreso_gasto)

        // Valida el input del editText cantidad en tiempo real
        edCantidad.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isValidDouble(s.toString())) {
                    edCantidad.error = "Por favor, ingresa un número válido"
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        /** Fin edCantidad */

        /** Inicio actvCategoria */
        // Inicializa el actvCategoria
        val actvCategoria: AutoCompleteTextView = findViewById(R.id.actv_categoria_ingreso_gasto)

        // Crea un ArrayAdapter con las opciones de registro
        val categorias = listOf("ingreso", "gasto")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categorias)

        // Asigna el adapter al actvCategoria
        actvCategoria.setAdapter(adapter)

        // Muestra la lista desplegable cuando el usuario haga click en el campo
        actvCategoria.setOnClickListener {
            actvCategoria.showDropDown()
        }
        /** Fin actvCategoria*/

        /** Inicio edFecha */
        // Inicializa el edFecha
        val edFecha: EditText = findViewById(R.id.ed_fecha_ingreso_gasto)
        // Instancia de Calendar para obtener la fecha actual
        val calendar = Calendar.getInstance()

        // Define un listener para manejar la fecha seleccionada en el DatePickerDialog
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            // Configura el Calendar con la fecha seleccionada
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            //Define el formato deseado
            val dateFormat = "dd/MM/yyyy" // Formato día, mes, año
            val sdf = SimpleDateFormat(dateFormat, Locale.US)

            // Actualiza el texto del edFecha con la fecha con el formato seleccionado
            edFecha.setText(sdf.format(calendar.time))
        }

        // Configura el evento de click para el edFecha
        edFecha.setOnClickListener {
            // Muestra el DatePickerDialog cuando se hace click en el edFecha
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        /** Fin edFecha */

        /** Inicio btnRegistrarIngresoGasto */
        // Inicializa el btnRegistrarIngresoGasto
        val btnRegistrar: Button = findViewById(R.id.btn_registrar_ingreso_gasto)

        // Configura el evento Click del boton
        btnRegistrar.setOnClickListener {
            // Le copie la idea a la Chely de solo mostrar un texto de aqui a que le añadamos
            // Funciones de guardado real al proyecto jijiji

            Toast.makeText(this, "Ingreso/Gasto registrado exitosamente", Toast.LENGTH_SHORT).show()
        }
        /** Fin btnRegistrarIngresoGasto */
    }

    // Valida que el input del edCantidad sea double
    fun isValidDouble(text: String): Boolean {
        return try {
            text.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }
}