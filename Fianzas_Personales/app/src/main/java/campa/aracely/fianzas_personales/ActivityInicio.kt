package campa.aracely.fianzas_personales

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView


class ActivityInicio : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        inicializarDrawer()
        configurarBotonDrawer()
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
            R.id.nav_historial -> ActivityHistorial::class.java
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
}
