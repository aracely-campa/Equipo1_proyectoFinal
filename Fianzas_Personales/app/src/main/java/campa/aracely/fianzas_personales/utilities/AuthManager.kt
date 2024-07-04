package campa.aracely.fianzas_personales.utilities

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object AuthManager {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var currentUser: FirebaseUser? = auth.currentUser

    val currentId: String?
        get() = currentUser?.uid


    fun refreshUser() {
        currentUser = auth.currentUser
    }

}