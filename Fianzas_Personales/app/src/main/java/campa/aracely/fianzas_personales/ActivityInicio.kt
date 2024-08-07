package campa.aracely.fianzas_personales

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import campa.aracely.fianzas_personales.Classes.Transaccion
import campa.aracely.fianzas_personales.Classes.TransaccionAdapter
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ActivityInicio : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransaccionAdapter
    private var transacciones = mutableListOf<Transaccion>()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var listenerRegistration: ListenerRegistration? = null
    private lateinit var btnPresupuesto: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        inicializarDrawer()
        configurarBotonDrawer()

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        btnPresupuesto = findViewById(R.id.btn_presupuesto)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TransaccionAdapter(transacciones) { transaccion ->
            eliminarTransaccion(transaccion)
        }
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val canScrollVertically = recyclerView.canScrollVertically(1) || recyclerView.canScrollVertically(-1)
                recyclerView.isNestedScrollingEnabled = canScrollVertically
            }
        })

        cargarTransacciones()

        btnPresupuesto.setOnClickListener {
            val intent = Intent(this, PresupuestoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cargarTransacciones() {
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val transaccionRef = firestore.collection("users").document(userId)
                .collection("transacciones")

            listenerRegistration = transaccionRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Error al cargar transacciones: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    transacciones.clear()
                    var presupuestoTotal = 0.0
                    for (document in snapshot.documents) {
                        val transaccion = document.toObject(Transaccion::class.java)
                        if (transaccion != null) {
                            transaccion.id = document.id
                            transacciones.add(transaccion)
                            if (transaccion.tipoGasto == "ingreso") {
                                presupuestoTotal += transaccion.cantidad
                            } else if (transaccion.tipoGasto == "gasto") {
                                presupuestoTotal -= transaccion.cantidad
                            }
                        }
                    }
                    actualizarPresupuesto(currentUser.uid, presupuestoTotal)
                    adapter.notifyDataSetChanged()
                }
                ajustarHabilidadDeDesplazamiento()
            }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ajustarHabilidadDeDesplazamiento() {
        recyclerView.isNestedScrollingEnabled = transacciones.size > 1
    }

    private fun actualizarPresupuesto(uid: String, nuevoPresupuesto: Double) {
        firestore.collection("users").document(uid)
            .update("presupuesto", nuevoPresupuesto)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Presupuesto actualizado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al actualizar el presupuesto", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun eliminarTransaccion(transaccion: Transaccion) {
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            firestore.collection("users").document(userId)
                .collection("transacciones").document(transaccion.id)
                .delete()
                .addOnSuccessListener {
                    transacciones.remove(transaccion)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Transacción eliminada", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("FireStoreDeletionError", "Error al eliminar transacción: ${e.message}")
                    Toast.makeText(this, "Error al eliminar transacción: ${e.message}", Toast.LENGTH_SHORT).show()
                }
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
            R.id.nav_registro_ingreso -> RegistroIngresosGastos::class.java
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

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }
}
