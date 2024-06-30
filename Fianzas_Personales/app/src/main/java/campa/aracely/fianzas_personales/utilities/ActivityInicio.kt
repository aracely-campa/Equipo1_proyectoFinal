package campa.aracely.fianzas_personales.utilities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import campa.aracely.fianzas_personales.GraficasActivity
import campa.aracely.fianzas_personales.MainActivity
import campa.aracely.fianzas_personales.R
import com.google.android.material.navigation.NavigationView

class ActivityInicio : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val btnOpenDrawer: ImageButton = findViewById(R.id.btn_open_drawer)
        btnOpenDrawer.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_registro_ingreso -> {
                val intent = Intent(this, Ingresos::class.java)
                startActivity(intent)
            }
            R.id.nav_historial -> {
                val intent = Intent(this, Ingresos::class.java)
                startActivity(intent)
            }
            R.id.nav_ver_gastos -> {
                val intent = Intent(this, GraficasActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_cerrar_sesion -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_ver_grafricas -> {
                val intent = Intent(this, GraficasActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
