import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import campa.aracely.fianzas_personales.R
import campa.aracely.fianzas_personales.utilities.Gastos
import campa.aracely.fianzas_personales.utilities.Transaccion
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class CustomCircleDrawable(context: Context, gastos: MutableList<Transaccion>) : Drawable() {

    private var grosorMetrica: Int = 0
    private var context: Context? = context
    private var gastos: ArrayList<Gastos> = gastos

    init {
        grosorMetrica = context.resources.getDimensionPixelSize(R.dimen.graphWith)
    }

    override fun draw(canvas: Canvas) {
        val ancho = canvas.width
        val alto = canvas.height
        val radius = min(ancho, alto) / 2.5f  // Ajuste del factor para el radio
        val centerX = ancho / 2.toFloat()
        val centerY = alto / 2.toFloat()

        // Dibujar fondo del círculo completo
        val fondo = Paint()
        fondo.style = Paint.Style.FILL
        fondo.color = ContextCompat.getColor(context!!, R.color.black)
        canvas.drawCircle(centerX, centerY, radius, fondo)

        // Dibujar secciones de emociones
        var anguloInicio = 0.0f
        for (gasto in gastos) {
            val porcentaje = gasto.porcentaje
            if (porcentaje == 0.0f) continue  // Saltar sección si el valor es 0

            val anguloBarrido = 360 * (porcentaje / 100)

            val seccion = Paint()
            seccion.style = Paint.Style.FILL
            seccion.color = ContextCompat.getColor(context!!, gasto.color)
            canvas.drawArc(
                RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius),
                anguloInicio, anguloBarrido, true, seccion
            )

            // Dibujar texto de la categoría en el centro de la sección
            if (porcentaje > 0) {
                val textoPaint = Paint()
                textoPaint.color = Color.BLACK  // Usar Color.WHITE directamente
                textoPaint.textSize = 30f
                textoPaint.textAlign = Paint.Align.CENTER
                textoPaint.isAntiAlias = true

                val textAngle = Math.toRadians((anguloInicio + anguloBarrido / 2).toDouble())
                val textX = (centerX + (radius / 2) * cos(textAngle)).toFloat()
                val textY = (centerY + (radius / 2) * sin(textAngle)).toFloat() + 10  // Ajuste para centrar verticalmente
                canvas.drawText(gasto.categoria, textX, textY, textoPaint)
            }

            anguloInicio += anguloBarrido
        }
    }

    override fun setAlpha(alpha: Int) {}

    override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {}

    override fun getOpacity(): Int {
        return android.graphics.PixelFormat.OPAQUE
    }
}
