package campa.aracely.fianzas_personales

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object AuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    var currentUser: FirebaseUser? = auth.currentUser

    val currentId: String?
        get() = currentUser?.uid


    fun refreshUser() {
        currentUser = auth.currentUser
    }

}