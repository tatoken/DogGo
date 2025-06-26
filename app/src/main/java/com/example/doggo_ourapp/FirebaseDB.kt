package com.example.doggo_ourapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate

object FirebaseDB {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDbRef: DatabaseReference = FirebaseDatabase.getInstance("https://doggo-6c19f-default-rtdb.europe-west1.firebasedatabase.app").getReference()

    fun signup(name: String, surname: String, birthDate: LocalDate, bio: String, email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@addOnCompleteListener
                    addUserToDatabase(name, surname, birthDate, bio, email, uid)
                    FirebaseAuth.getInstance().signOut()
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
    }

    private fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
    }


    private fun addUserToDatabase(name: String, surname:String, birthDate: LocalDate,bio:String,email: String, uid: String) {
        mDbRef.child("user").child(uid).setValue(User(name,surname,birthDate,bio,email,uid))
    }

}
