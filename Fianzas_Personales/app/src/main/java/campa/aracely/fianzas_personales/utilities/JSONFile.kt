package campa.aracely.fianzas_personales.utilities

import android.content.Context
import android.util.Log
import java.io.IOException

class JSONFile {
    val MIS_GASTOS = "data.json"

    constructor()

    fun saveData(context: Context, json: String) {
        try {
            context.openFileOutput(MIS_GASTOS, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
        } catch (e: IOException) {
            Log.e("GUARDAR", "Error in Writing: " + e.localizedMessage)
        }
    }

    fun getData(context: Context): String {
        try {

            return context.openFileInput(MIS_GASTOS).bufferedReader().readLine()
        } catch (e: IOException) {
            Log.e("OBTENER", "Error in fetching data: " + e.localizedMessage)
            return ""
        }
    }



}