package campa.aracely.fianzas_personales

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import campa.aracely.fianzas_personales.utilities.ActivityInicio
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActivityRegistro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Inicializa todos los editText del xml
        val etNombre: EditText = findViewById(R.id.et_nombre)
        val etApellidos: EditText = findViewById(R.id.et_apellidos)
        val etCorreo: EditText = findViewById(R.id.et_correo)
        val etClave: EditText = findViewById(R.id.et_clave)
        val etConfirmarClave: EditText = findViewById(R.id.et_confirmar_clave)
        val etFechaNacimiento: EditText = findViewById(R.id.et_fecha_nacimiento)
        val cbTerminos: CheckBox = findViewById(R.id.cb_terminos)
        val btnRegistrar: Button = findViewById(R.id.btn_registrar)

        /** Inicio Configuracion y validacion del etFechaNacimiento */
        // Configura el calendar y DatePickerDialog
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            // Configura el calendar con la fecha seleccionada
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Define el formato deseado
            val dateFormat = "dd/MM/yyyy" //dia, mes, año
            val sdf = SimpleDateFormat(dateFormat, Locale.US)

            // Actualiza el texto del etFechaNacimiento con la fecha formateada
            etFechaNacimiento.setText(sdf.format(calendar.time))
        }

        // Evento click para el etFechaNacimiento
        etFechaNacimiento.setOnClickListener {
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
            ).show()
        }
        /** Fin configuracion etFechaNacimiento */

        /** Inicio validacion correo */
        etCorreo.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val email = it.toString()
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        etCorreo.error = "Correo electrónico no válido"
                    } else {
                        etCorreo.error = null
                    }
                }
            }
        })
        /** Fin validacion correo */

        /** Inicio validacion contraseña y confirmar contraseña */
        // Contraseña
        etClave.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val password = it.toString()
                    if (password.length < 6) {
                        etClave.error = "La contraseña debe tener al menos 6 caracteres"
                    } else {
                        etClave.error = null
                    }
                }
            }
        })

        // Confirmar contraseña
        etConfirmarClave.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val confirmPassword = it.toString()
                    val password = etClave.text.toString()
                    if (confirmPassword != password) {
                        etConfirmarClave.error = "Las contraseñas no coinciden"
                    } else {
                        etConfirmarClave.error = null
                    }
                }
            }
        })
        /** Fin validacion contraseña y confirmar contraseña */

        /** Inicio Boton registrar ClickListener */
        btnRegistrar.setOnClickListener {
            if (camposValidos(etNombre, etApellidos, etCorreo, etClave, etConfirmarClave, etFechaNacimiento, cbTerminos)) {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                val fechaNacimiento = sdf.parse(etFechaNacimiento.text.toString())
                val edad = getEdad(fechaNacimiento)

                if(edad >= 18){
                    Toast.makeText(this, "Registro éxitoso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ActivityInicio::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Debe ser mayor de edad para registrarte", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }
        /** Fin Boton registrar ClickListener */
    }

    // Funcion para validar los campos de texto y el checkBox de terminos y condiciones
    fun camposValidos(etNombre: EditText,
                      etApellidos: EditText,
                      etCorreo: EditText,
                      etClave: EditText,
                      etConfirmarClave: EditText,
                      etFechaNacimiento:EditText,
                      cbTerminos: CheckBox
    ): Boolean {
        val nombreNoVacio = etNombre.text.isNotEmpty()
        val apellidoNoVacio = etApellidos.text.isNotEmpty()
        val correoValido = etCorreo.error == null
        val claveValida = etClave.text.length >= 6
        val confirmarClaveValida = etConfirmarClave.error == null && etConfirmarClave.text.toString() == etClave.text.toString()
        val fechaNacimientoValida = etFechaNacimiento.text.isNotEmpty()
        val terminosAceptados = cbTerminos.isChecked

        if(!nombreNoVacio) {
            etNombre.error = "El campo de nombre(s) está vacío"
        }

        if(!apellidoNoVacio) {
            etApellidos.error = "El campo de apellidos esta vacio"
        }

        if(!correoValido) {
            etCorreo.error = "El correo electrónico no es válido"
        }

        if(!claveValida) {
            etClave.error = "La contraseña debe tener al menos 6 cáracteres"
        }

        if(!confirmarClaveValida) {
            etConfirmarClave.error = "Las contraseñas no coinciden"
        }

        if(!fechaNacimientoValida) {
            etFechaNacimiento.error = "La fecha de nacimiento es obligatoria"
        }

        if(!terminosAceptados) {
            cbTerminos.error = "Debe aceptar los términos y condiciones"
        }

        return nombreNoVacio && apellidoNoVacio && correoValido && claveValida && confirmarClaveValida && fechaNacimientoValida && terminosAceptados
    }

    // Funcion para asegurarse de que la fecha registrada es de una persona mayor de edad
    fun getEdad(fechaNacimiento: Date): Int {
        val hoy = Calendar.getInstance()
        val nacimientoCalendario = Calendar.getInstance()
        nacimientoCalendario.time = fechaNacimiento

        var edad = hoy.get(Calendar.YEAR) - nacimientoCalendario.get(Calendar.YEAR)

        if (hoy.get(Calendar.DAY_OF_YEAR) < nacimientoCalendario.get(Calendar.DAY_OF_YEAR)) {
            edad--
        }

        return edad
    }
}