package campa.aracely.fianzas_personales.utilities

data class Transaccion (val categoria: String,
                        val cantidad: Int,
                        val descripcion: String,
                        val fecha: String,
                        val tipoGasto: String)