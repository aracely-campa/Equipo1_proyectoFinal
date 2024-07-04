package campa.aracely.fianzas_personales

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
import com.google.firebase.auth.FirebaseAuth

class ActivityRecuperarContrasena : AppCompatActivity() {
    private lateinit var etCorreo: EditText
    private lateinit var btnEnviar: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contrasena)

        inicializarVistas()
        auth = FirebaseAuth.getInstance()

        btnEnviar.setOnClickListener {
            manejarResetPassword()
        }

        configurarValidacionCorreo()
    }

    private fun inicializarVistas() {
        etCorreo = findViewById(R.id.et_correo)
        btnEnviar = findViewById(R.id.btn_enviar)
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

    private fun manejarResetPassword() {
        val correo = etCorreo.text.toString().trim()

        if (correo.isEmpty()) {
            etCorreo.error = getString(R.string.mensaje_error_correo)
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.error = getString(R.string.mensaje_error_correo)
            return
        }

        auth.sendPasswordResetEmail(correo)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "getString(R.string.mensaje_reset_password_exito)", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "getString(R.string.mensaje_error_reset_password)" + ": " + task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}