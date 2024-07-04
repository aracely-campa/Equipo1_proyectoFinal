import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import campa.aracely.fianzas_personales.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class CustomCircleDrawable(context: Context, private val data: List<Triple<Float, Int, String>>) : Drawable() {

    private val grosorMetrica: Int = context.resources.getDimensionPixelSize(R.dimen.graphWith)
    private val context: Context = context

    override fun draw(canvas: Canvas) {
        val ancho = canvas.width
        val alto = canvas.height
        val radius = (min(ancho, alto) / 2.5f) - grosorMetrica
        val centerX = ancho / 2f
        val centerY = alto / 2f


        val fondo = Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.black)
        }
        canvas.drawCircle(centerX, centerY, radius, fondo)

        var anguloInicio = 0.0f
        for ((porcentaje, color, categoria) in data) {
            if (porcentaje == 0.0f) continue  // Saltar sección si el valor es 0

            val anguloBarrido = 360 * (porcentaje / 100)

            val seccion = Paint().apply {
                style = Paint.Style.FILL
                this.color = color
            }
            canvas.drawArc(
                RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius),
                anguloInicio, anguloBarrido, true, seccion
            )

            // Dibujar texto de la categoría en el centro de la sección
            val textoPaint = Paint().apply {
                var color = Color.WHITE
                textSize = 30f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            val textAngle = Math.toRadians((anguloInicio + anguloBarrido / 2).toDouble())
            val textX = (centerX + (radius / 2) * cos(textAngle)).toFloat()
            val textY = (centerY + (radius / 2) * sin(textAngle)).toFloat() + 10  // Ajuste para centrar verticalmente
            canvas.drawText(categoria, textX, textY, textoPaint)

            anguloInicio += anguloBarrido
        }
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {}
    override fun getOpacity(): Int = android.graphics.PixelFormat.OPAQUE
}
