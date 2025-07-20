package com.example.doggo_ourapp

import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate

object UserFirebase {
    fun signup(name: String, surname: String, birthDate: LocalDate, bio: String, email: String, password: String, onResult: (Boolean) -> Unit) {
        FirebaseDB.getAuth().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@addOnCompleteListener
                    addUserToDatabase(name, surname, birthDate, bio, email, uid)
                    FirebaseDB.getAuth().signOut()
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
    }

    private fun addUserToDatabase(name: String, surname:String, birthDate: LocalDate, bio:String, email: String, uid: String) {
        FirebaseDB.getMDbRef().child("user").child(uid).setValue(UserData(name,surname,birthDate,bio,email,uid))
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        FirebaseDB.getAuth().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
    }

    fun getCurrentUserId():String
    {
        return FirebaseDB.getAuth().currentUser?.uid ?: return ""
    }

    fun getCurrentUserPoints(onResult: (String?) -> Unit)
    {
        val dbRef = FirebaseDB.getMDbRef()
        val userRef = dbRef.child("user").child(getCurrentUserId())

        userRef.child("points").get().addOnSuccessListener { snapshotPoints ->
            val pointsStr = snapshotPoints.getValue(String::class.java)
            onResult(pointsStr)
        }
    }
}