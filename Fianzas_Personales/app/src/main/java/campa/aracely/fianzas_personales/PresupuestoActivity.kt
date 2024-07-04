package campa.aracely.fianzas_personales

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class PresupuestoActivity : AppCompatActivity() {

    private lateinit var etDineroCuenta: EditText
    private lateinit var btnRegistrarPresupuesto: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var btnVolver: ImageButton

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
        auth = FirebaseAuth.getInstance()

        btnRegistrarPresupuesto.setOnClickListener {
            registrarPresupuesto()
        }

    }

    private fun registrarPresupuesto() {
        val dineroCuentaStr = etDineroCuenta.text.toString()
        Log.d("PresupuestoActivity", "Valor ingresado: $dineroCuentaStr")
        val dineroCuenta = dineroCuentaStr.toIntOrNull()

        if (dineroCuenta == null || dineroCuenta <= 0) {
            Toast.makeText(this, "Por favor ingrese una cantidad válida mayor que 0", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("PresupuestoActivity", "Valor convertido: $dineroCuenta")

        val uid = auth.currentUser?.uid
        if (uid != null) {
            val userRef = firestore.collection("users").document(uid)
            userRef.set(mapOf("presupuesto" to dineroCuenta), SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("PresupuestoActivity", "Presupuesto actualizado: $dineroCuenta")
                    Toast.makeText(this, "Presupuesto registrado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { exception ->
                    Log.e("PresupuestoActivity", "Error al registrar el presupuesto", exception)
                    Toast.makeText(this, "Error al registrar el presupuesto: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No se pudo obtener el usuario", Toast.LENGTH_SHORT).show()
        }
    }
}
