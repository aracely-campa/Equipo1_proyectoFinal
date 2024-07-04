package campa.aracely.fianzas_personales.utilities

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import campa.aracely.fianzas_personales.R

class CustomLineDrawable(
    private val context: Context,  // Añade una propiedad para almacenar el contexto
    private val porcentaje: Float,
    private val colorResId: Int
) : Drawable() {

    private val paint = Paint()

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f  // Grosor de la línea
        paint.isAntiAlias = true
    }

    override fun draw(canvas: Canvas) {
        val width = bounds.width().toFloat()
        val height = bounds.height().toFloat()

        // Dibuja el fondo negro
        val fondo = Paint()
        fondo.style = Paint.Style.FILL
        fondo.color = ContextCompat.getColor(context, R.color.black)
        canvas.drawRect(0f, 0f, width, height, fondo)

        // Dibuja la línea de acuerdo al porcentaje
        if (porcentaje > 0) {
            val porcentajeWidth = porcentaje * width / 100
            paint.color = ContextCompat.getColor(context, colorResId)
            val startY = height / 2
            val endX = porcentajeWidth
            val endY = height / 2
            canvas.drawLine(0f, startY, endX, endY, paint)
        }
    }

    override fun setAlpha(alpha: Int) {}

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }
}
