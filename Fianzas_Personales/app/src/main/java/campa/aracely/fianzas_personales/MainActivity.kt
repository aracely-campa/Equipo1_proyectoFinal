package campa.aracely.fianzas_personales

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val graficas: Button = findViewById(R.id.graficas)
        val login: Button = findViewById(R.id.Login)

        graficas.setOnClickListener {
            var intento = Intent(this, GraficasActivity::class.java)
            this.startActivity(intento)
        }

        login.setOnClickListener{
            var intento = Intent(this, Activity_Login ::class.java)
            this.startActivity(intento)
        }

    }
}