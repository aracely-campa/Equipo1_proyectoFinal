package campa.aracely.fianzas_personales

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import campa.aracely.fianzas_personales.utilities.Transaccion
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class ActivityInicio : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var listViewHistorial: ListView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        inicializarDrawer()
        configurarBotonDrawer()
        configurarBotonRegistrar()
        listViewHistorial = findViewById(R.id.lista_historial)
        firestore = FirebaseFirestore.getInstance()
        cargarHistorial()
    }

    private fun configurarBotonRegistrar() {
        val btnNuevaTransaccion: Button = findViewById(R.id.btn_nuevaTransaccion)
        btnNuevaTransaccion.setOnClickListener {
            val intent = Intent(this, RegistroIngresosGastos::class.java)
            startActivity(intent)
        }
    }

    private fun inicializarDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)
    }

    private fun configurarBotonDrawer() {
        val btnOpenDrawer: ImageButton = findViewById(R.id.btn_open_drawer)
        btnOpenDrawer.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val destino = when (item.itemId) {
            R.id.nav_ver_gastos -> GraficasActivity::class.java
            R.id.nav_cerrar_sesion -> MainActivity::class.java
            R.id.nav_ver_grafricas -> GraficasActivity::class.java
            else -> null
        }
        destino?.let { navegarA(it) }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun navegarA(destino: Class<*>) {
        startActivity(Intent(this, destino))
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun cargarHistorial() {
        firestore.collection("ingresos_gastos")
            .get()
            .addOnSuccessListener { result ->
                val transacciones = mutableListOf<Transaccion>()
                for (document in result) {
                    val cantidad = document.getDouble("cantidad")
                    val fecha = document.getString("fecha")
                    val categoria = document.getString("categoria")
                    val descripcion = document.getString("descripcion")
                    val tipo = document.getString("tipo")

                    if (cantidad != null && fecha != null && categoria != null && descripcion != null && tipo != null) {
                        transacciones.add(Transaccion(cantidad, fecha, categoria, descripcion, tipo))
                    }
                }
                val adapter = TransaccionAdapter(this, transacciones)
                listViewHistorial.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar historial: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
