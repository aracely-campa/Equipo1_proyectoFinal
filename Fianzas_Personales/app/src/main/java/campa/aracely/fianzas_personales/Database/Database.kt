package campa.aracely.fianzas_personales.Database

import com.google.firebase.firestore.FirebaseFirestore

class Database {

    private lateinit var db: FirebaseFirestore

    fun getDatabase(): FirebaseFirestore {
        if (!this::db.isInitialized) {
            db = FirebaseFirestore.getInstance()
        }
        return db
    }
}
