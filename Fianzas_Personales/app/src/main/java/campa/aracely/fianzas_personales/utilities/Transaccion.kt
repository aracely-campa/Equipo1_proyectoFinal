package campa.aracely.fianzas_personales.utilities

data class Transaccion(
    var id: String = "",
    val categoria: String = "",
    val cantidad: Double = 0.0,
    val descripcion: String = "",
    val fecha: String = "",
    val tipoGasto: String = ""
)
