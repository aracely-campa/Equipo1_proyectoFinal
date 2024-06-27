package campa.aracely.fianzas_personales

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnIniciarSesion: Button = findViewById(R.id.btn_iniciarSesion)
        btnIniciarSesion.setOnClickListener {
            val intent = Intent(this, Activity_Login::class.java)
            startActivity(intent)
        }

        val registerText: TextView = findViewById(R.id.register_text)
        registerText.setOnClickListener {
            val intent = Intent(this, ActivityRegistro::class.java) // Asegúrate de que ActivityRegistro es la actividad de registro
            startActivity(intent)
        }
    }
}
