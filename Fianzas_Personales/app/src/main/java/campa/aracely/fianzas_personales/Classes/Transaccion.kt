package campa.aracely.fianzas_personales.Classes

data class Transaccion(
    var id: String = "",
    val categoria: String = "",
    val cantidad: Float = 0.0f,
    val descripcion: String = "",
    val fecha: String = "",
    val tipoGasto: String = ""
)
