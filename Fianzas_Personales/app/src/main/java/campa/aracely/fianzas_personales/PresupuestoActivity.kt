package campa.aracely.fianzas_personales

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import campa.aracely.fianzas_personales.Database.Database
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class PresupuestoActivity : AppCompatActivity() {

    private lateinit var etDineroCuenta: EditText
    private lateinit var btnRegistrarPresupuesto: Button
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_presupuesto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etDineroCuenta = findViewById(R.id.et_dinero_cuenta)
        btnRegistrarPresupuesto = findViewById(R.id.btn_registrar_presupuesto)
        firestore = FirebaseFirestore.getInstance()

        btnRegistrarPresupuesto.setOnClickListener {
            registrarPresupuesto()
        }
    }

    private fun registrarPresupuesto() {
        val dineroCuenta = etDineroCuenta.text.toString()
        if(dineroCuenta.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese una cantidad", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if(uid != null) {
            val userMap = hashMapOf("presupuesto" to dineroCuenta)
            firestore.collection("users").document(uid).set(userMap, SetOptions.merge())
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(this, "Presupuesto registrado correctamente", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ActivityInicio::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Error al registrar el presupuesto", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "No se pudo obtener el usuario", Toast.LENGTH_SHORT).show()
        }
    }
}