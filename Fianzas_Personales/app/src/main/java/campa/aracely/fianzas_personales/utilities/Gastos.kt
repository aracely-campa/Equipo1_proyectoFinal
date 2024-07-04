package campa.aracely.fianzas_personales.utilities

import java.io.Serializable
import java.time.LocalDate

class Gastos(
    var nombre: String,
    var porcentaje: Float,
    var color: Int,
    var monto: Float,
    var categoria: String,
    var fecha: LocalDate,
    var tipo: String
) : Serializable