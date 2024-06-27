package campa.aracely.fianzas_personales

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Activity_Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etCorreo: EditText = findViewById(R.id.et_correo)
        val etClave: EditText = findViewById(R.id.et_clave)
        val btnIniciarSesion: Button = findViewById(R.id.btn_iniciarSesion)

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
        fun camposValidos(etCorreo: EditText, etClave: EditText): Boolean {
            val correoValido = etCorreo.error == null
            val claveValida = etClave.text.isNotEmpty()

            if (!correoValido) {
                etCorreo.error = "El correo electrónico no es válido"
            }
            if (!claveValida) {
                etClave.error = "La contraseña es obligatoria"
            }
            return correoValido && claveValida
        }

        btnIniciarSesion.setOnClickListener {
            if (camposValidos(etCorreo, etClave)) {
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ActivityInicio::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }
}