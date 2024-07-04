package campa.aracely.fianzas_personales

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class Activity_Login : AppCompatActivity() {
    private lateinit var etCorreo: EditText
    private lateinit var etClave: EditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var tvOlvidasteContrasena: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        enableEdgeToEdge()
        configurarBordesVentana()
        inicializarVistas()
        configurarValidacionCorreo()

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        btnIniciarSesion.setOnClickListener {
            manejarInicioSesion()
        }

        tvOlvidasteContrasena.setOnClickListener {
            val intent = Intent(this, ActivityRecuperarContrasena::class.java)
            startActivity(intent)
        }
    }

    private fun configurarBordesVentana() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun inicializarVistas() {
        tvOlvidasteContrasena = findViewById(R.id.tv_olvidaste_contrasena)
        etCorreo = findViewById(R.id.et_correo)
        etClave = findViewById(R.id.et_clave)
        btnIniciarSesion = findViewById(R.id.btn_iniciarSesion)
    }

    private fun configurarValidacionCorreo() {
        etCorreo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validarCorreo(s)
            }
        })
    }

    private fun validarCorreo(s: Editable?) {
        s?.let {
            val email = it.toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etCorreo.error = getString(R.string.mensaje_error_correo)
            } else {
                etCorreo.error = null
            }
        }
    }

    private fun manejarInicioSesion() {
        if (camposValidos()) {
            val correo = etCorreo.text.toString()
            val clave = etClave.text.toString()

            auth.signInWithEmailAndPassword(correo, clave)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, getString(R.string.mensaje_inicio_sesion), Toast.LENGTH_SHORT).show()
                        navegarAPrincipal()
                    } else {
                        Toast.makeText(this, getString(R.string.mensaje_error_inicio_sesion) + ": " + task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, getString(R.string.mensaje_error_inicio_sesion), Toast.LENGTH_SHORT).show()
        }
    }

    private fun camposValidos(): Boolean {
        val correoValido = etCorreo.error == null
        val claveValida = etClave.text.isNotEmpty()

        if (!correoValido) {
            etCorreo.error = getString(R.string.mensaje_error_correo)
        }
        if (!claveValida) {
            etClave.error = getString(R.string.mensaje_contraseña_obligatoria)
        }

        return correoValido && claveValida
    }

    private fun navegarAPrincipal() {
        val intent = Intent(this, ActivityInicio::class.java)
        startActivity(intent)
        finish() // Opcional: finaliza esta actividad para que el usuario no pueda volver atrás sin cerrar sesión
    }
}
