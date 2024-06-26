package campa.aracely.fianzas_personales.utilities

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import campa.aracely.fianzas_personales.R

class CustomBarDrawable: Drawable {

    var coordenadas: RectF?=null
    var context: Context?=null
    var gastos: Gastos?=null

    constructor(context: Context, gastos:Gastos){
        this.context=context
        this.gastos=gastos
    }


    override fun draw(p0: Canvas) {
        val fondo: Paint = Paint()
        fondo.style = Paint.Style.FILL
        fondo.isAntiAlias = true

        //Aqui le mueves el color a sepa que cosa, ahi te digo despues
        fondo.color = context?.resources?.getColor(R.color.black) ?: R.color.black

        val ancho: Float = (p0.width - 10).toFloat()
        val alto: Float = (p0.height - 10).toFloat()

        val coordenadas = RectF(0.0F, 0.0F,  ancho,  alto)

        p0.drawRect(coordenadas!!, fondo)

        if (this.gastos != null) {
            val porcentaje: Float = this.gastos!!.porcentaje * (p0.width - 10) / 100
            val coordenadas2 = RectF(0.0F,  0.0F,  porcentaje, alto)

            val seccion: Paint = Paint()
            seccion.style = Paint.Style.FILL
            seccion.isAntiAlias = true
            seccion.color = ContextCompat.getColor(this.context!!, this.gastos!!.color)
            p0.drawRect(coordenadas2!!, seccion)
        }
    }


    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }
}