package campa.aracely.fianzas_personales

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import campa.aracely.fianzas_personales.utilities.CustomLineDrawable
import campa.aracely.fianzas_personales.utilities.Gastos
import java.time.LocalDate

class GraficaLineaActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grafica_linea)

        // Ejemplo de lista de gastos (puedes obtenerla de tu fuente de datos real)
        val listaGastos = mutableListOf(
            Gastos("Alimentación", 40f, R.color.blue, 400f, "Alimentos", LocalDate.now(),"Ingreso"),
            Gastos("Transporte", 20f, R.color.green, 200f, "Transporte", LocalDate.now(),"Ingreso"),
            Gastos("Entretenimiento", 30f, R.color.orange, 300f, "Entretenimiento", LocalDate.now(),"Ingreso"),
            Gastos("Otros", 10f, R.color.red, 100f, "Otros", LocalDate.now(),"Ingreso")
        )

        // Obtén una referencia al ImageView donde dibujar la gráfica (graph)
        val graphView = findViewById<ImageView>(R.id.graph)

        // Limpia cualquier overlay previo para evitar duplicados
        graphView.overlay.clear()

        // Dibuja cada línea de acuerdo a los porcentajes de cada gasto
        listaGastos.forEachIndexed { index, gasto ->
            val porcentaje = gasto.porcentaje
            val colorResId = gasto.color

            // Crea una instancia de CustomLineDrawable con el contexto, porcentaje y color correspondiente
            val customLineDrawable = CustomLineDrawable(this, porcentaje, ContextCompat.getColor(this, colorResId))

            // Establece los límites de dibujo dentro del ImageView
            customLineDrawable.setBounds(0, 0, graphView.width, graphView.height)

            // Agrega cada línea a la vista de gráfica
            graphView.overlay.add(customLineDrawable)
        }
    }
}
