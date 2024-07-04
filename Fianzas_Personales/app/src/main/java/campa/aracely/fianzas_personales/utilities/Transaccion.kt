package campa.aracely.fianzas_personales.utilities

data class Transaccion(
    val cantidad: Double,
    val categoria: String,
    val tipo: String,
    val fecha: String,
    val descripcion: String
)
