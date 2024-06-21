package campa.aracely.fianzas_personales

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GraficasActivity : AppCompatActivity() {

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_graficas)

        val spinner: Spinner = findViewById(R.id.spinner_mes)
        val meses = listOf(
            getString(R.string.enero),
            getString(R.string.febrero),
            getString(R.string.marzo),
            getString(R.string.abril),
            getString(R.string.mayo),
            getString(R.string.junio),
            getString(R.string.julio),
            getString(R.string.agosto),
            getString(R.string.septiembre),
            getString(R.string.octubre),
            getString(R.string.noviembre),
            getString(R.string.diciembre)
        )
    val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,meses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter=adapter
        spinner.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                val selectedItem = meses[position]
                Toast.makeText(this@GraficasActivity, "Enjoy the movie", Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }
}