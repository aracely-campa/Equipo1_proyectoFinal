package campa.aracely.fianzas_personales

import android.annotation.SuppressLint
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
import campa.aracely.fianzas_personales.Database.Database
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActivityRegistro : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etClave: EditText
    private lateinit var etConfirmarClave: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var cbTerminos: CheckBox
    private lateinit var btnRegistrar: Button
    private lateinit var calendar: Calendar

    private lateinit var database: Database
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        configurarBordesVentana()
        inicializarVistas()
        configurarDatePicker()
        configurarValidacionCampos()
        configurarBotonRegistrar()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    private fun configurarBordesVentana() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun inicializarVistas() {
        etNombre = findViewById(R.id.et_nombre)
        etApellidos = findViewById(R.id.et_apellidos)
        etCorreo = findViewById(R.id.et_correo)
        etClave = findViewById(R.id.et_clave)
        etConfirmarClave = findViewById(R.id.et_confirmar_clave)
        etFechaNacimiento = findViewById(R.id.et_fecha_nacimiento)
        cbTerminos = findViewById(R.id.cb_terminos)
        btnRegistrar = findViewById(R.id.btn_registrar)
        calendar = Calendar.getInstance()
    }

    private fun configurarDatePicker() {
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

    private fun configurarValidacionCampos() {
        configurarValidacionCampo(etCorreo, ::validarCorreo)
        configurarValidacionCampo(etClave, ::validarClave)
        configurarValidacionCampo(etConfirmarClave, ::validarConfirmacionClave)
    }

    private fun configurarValidacionCampo(campo: EditText, validar: (Editable?) -> Unit) {
        campo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validar(s)
            }
        })
    }

    private fun validarCorreo(s: Editable?) {
        s?.let {
            val email = it.toString()
            etCorreo.error = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                getString(R.string.mensaje_correo_no_valido)
            } else {
                null
            }
        }
    }

    private fun validarClave(s: Editable?) {
        s?.let {
            val password = it.toString()
            etClave.error = if (password.length < 6) {
                getString(R.string.mensaje_error_contraseña)
            } else {
                null
            }
        }
    }

    private fun validarConfirmacionClave(s: Editable?) {
        s?.let {
            val confirmPassword = it.toString()
            val password = etClave.text.toString()
            etConfirmarClave.error = if (confirmPassword != password) {
                getString(R.string.mensaje_error_confirmar_clave)
            } else {
                null
            }
        }
    }

    private fun configurarBotonRegistrar() {
        btnRegistrar.setOnClickListener {
            if (camposValidos()) {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                val fechaNacimiento = sdf.parse(etFechaNacimiento.text.toString())
                val edad = getEdad(fechaNacimiento)

                if (edad >= 18) {
                    Toast.makeText(
                        this,
                        getString(R.string.mensaje_registro_exitoso),
                        Toast.LENGTH_SHORT
                    ).show()
                    registrarUsuario()
                } else {
                    Toast.makeText(this, getString(R.string.mensaje_error_edad), Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.mensaje_error_registrar),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun camposValidos(): Boolean {
        val nombreNoVacio = etNombre.text.isNotEmpty()
        val apellidoNoVacio = etApellidos.text.isNotEmpty()
        val correoValido = etCorreo.error == null
        val claveValida = etClave.text.length >= 6
        val confirmarClaveValida =
            etConfirmarClave.error == null && etConfirmarClave.text.toString() == etClave.text.toString()
        val fechaNacimientoValida = etFechaNacimiento.text.isNotEmpty()
        val terminosAceptados = cbTerminos.isChecked

        if (!nombreNoVacio) etNombre.error = getString(R.string.mensaje_error_nombre)
        if (!apellidoNoVacio) etApellidos.error = getString(R.string.mensaje_error_apellidos)
        if (!correoValido) etCorreo.error = getString(R.string.mensaje_error_correo_registro)
        if (!claveValida) etClave.error = getString(R.string.mensaje_error_clave)
        if (!confirmarClaveValida) etConfirmarClave.error =
            getString(R.string.mensaje_error_confirmar_clave)
        if (!fechaNacimientoValida) etFechaNacimiento.error =
            getString(R.string.mensaje_error_fecha)
        if (!terminosAceptados) cbTerminos.error = getString(R.string.mensaje_error_terminos)

        return nombreNoVacio && apellidoNoVacio && correoValido && claveValida && confirmarClaveValida && fechaNacimientoValida && terminosAceptados
    }

    private fun getEdad(fechaNacimiento: Date): Int {
        val hoy = Calendar.getInstance()
        val nacimientoCalendario = Calendar.getInstance().apply { time = fechaNacimiento }
        var edad = hoy.get(Calendar.YEAR) - nacimientoCalendario.get(Calendar.YEAR)

        if (hoy.get(Calendar.DAY_OF_YEAR) < nacimientoCalendario.get(Calendar.DAY_OF_YEAR)) {
            edad--
        }

        return edad
    }

    @SuppressLint("StringFormatInvalid")
    private fun registrarUsuario() {
        val nombre = etNombre.text.toString()
        val apellidos = etApellidos.text.toString()
        val correo = etCorreo.text.toString()
        val clave = etClave.text.toString()
        val fechaNacimiento = etFechaNacimiento.text.toString()

        auth.createUserWithEmailAndPassword(correo, clave)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid

                    val userMap = hashMapOf(
                        "nombre" to nombre,
                        "apellidos" to apellidos,
                        "correoElectronico" to correo,
                        "fechaNacimiento" to fechaNacimiento
                    )

                    if (userId != null) {
                        firestore.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, getString(R.string.mensaje_registro_exitoso), Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, ActivityInicio::class.java))
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, getString(R.string.mensaje_error_firebase, e.message), Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, getString(R.string.mensaje_error_registrar_firebase, task.exception?.message), Toast.LENGTH_SHORT).show()
                }
            }
    }
}
