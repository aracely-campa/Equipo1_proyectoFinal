package campa.aracely.fianzas_personales

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val graficas: Button = findViewById(R.id.graficas)
        val login: Button = findViewById(R.id.Login)
        val registroIngresos: Button = findViewById(R.id.registro_ingresos)

        graficas.setOnClickListener {
            val intent = Intent(this, GraficasActivity::class.java)
            startActivity(intent)
        }

        login.setOnClickListener {
            val intent = Intent(this, Activity_Login::class.java)
            startActivity(intent)
        }

        registroIngresos.setOnClickListener {
            val intent = Intent(this, RegistroIngresosGastos::class.java)
            startActivity(intent)
        }
    }
}