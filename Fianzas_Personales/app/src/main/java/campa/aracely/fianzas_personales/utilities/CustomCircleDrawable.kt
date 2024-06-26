import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import campa.aracely.fianzas_personales.R
import campa.aracely.fianzas_personales.utilities.Gastos
import kotlin.math.min

class CustomCircleDrawable(context: Context, gastos: ArrayList<Gastos>) : Drawable() {

    private var grosorMetrica: Int = 0
    private var context: Context? = context
    private var gastos: ArrayList<Gastos> = gastos

    init {
        grosorMetrica = context.resources.getDimensionPixelSize(R.dimen.graphWith)
    }

    override fun draw(canvas: Canvas) {
        val ancho = canvas.width
        val alto = canvas.height
        val radius = min(ancho, alto) / 2.toFloat()
        val centerX = ancho / 2.toFloat()
        val centerY = alto / 2.toFloat()

        // Dibujar fondo del círculo completo
        val fondo = Paint()
        fondo.style = Paint.Style.FILL
        fondo.color = ContextCompat.getColor(context!!, R.color.black)
        canvas.drawCircle(centerX, centerY, radius, fondo)

        // Dibujar secciones de gastos
        var anguloInicio = 0.0f
        for (gasto in gastos) {
            val porcentaje = gasto.porcentaje
            val anguloBarrido = 360 * (porcentaje / 100)

            val seccion = Paint()
            seccion.style = Paint.Style.FILL
            seccion.color = ContextCompat.getColor(context!!, gasto.color)
            canvas.drawArc(RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius),
                anguloInicio, anguloBarrido, true, seccion)

            // Dibujar texto en el centro de cada sección
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = ContextCompat.getColor(context!!, android.R.color.white)
                textAlign = Paint.Align.CENTER
                textSize = context!!.resources.getDimensionPixelSize(R.dimen.textSize).toFloat()
            }
            val text = "${gasto.categoria}: ${porcentaje.toInt()}%"
            val textX = centerX + radius / 2 * Math.cos(Math.toRadians((anguloInicio + anguloBarrido / 2).toDouble())).toFloat()
            val textY = centerY + radius / 2 * Math.sin(Math.toRadians((anguloInicio + anguloBarrido / 2).toDouble())).toFloat()
            canvas.drawText(text, textX, textY, textPaint)

            anguloInicio += anguloBarrido
        }
    }

    override fun setAlpha(alpha: Int) {}

    override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {}

    override fun getOpacity(): Int {
        return android.graphics.PixelFormat.OPAQUE
    }
}
