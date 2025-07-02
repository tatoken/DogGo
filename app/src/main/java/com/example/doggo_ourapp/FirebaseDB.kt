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
                    auth.signOut()
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
    }

    private fun addUserToDatabase(name: String, surname:String, birthDate: LocalDate,bio:String,email: String, uid: String) {
        mDbRef.child("user").child(uid).setValue(UserData(name,surname,birthDate,bio,email,uid))
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

    public fun getCurrentUserId():String
    {
        return auth.currentUser?.uid ?: return ""
    }

    fun saveDog(dog: DogData) {
        val userId = auth.currentUser?.uid ?: return
        val dogRef = mDbRef.child("user").child(userId).child("dog").push() // genera un ID univoco
        dog.id = dogRef.key
        dogRef.setValue(dog)
    }


    fun loadAllDog(onResult: (List<DogData>?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val dogRef = mDbRef.child("user").child(userId).child("dog")

        dogRef.get().addOnSuccessListener { dataSnapshot ->
            val dogList = mutableListOf<DogData>()
            for (child in dataSnapshot.children) {
                val dog = child.getValue(DogData::class.java)
                dog?.let {
                    it.id = child.key // assegna l'ID Firebase al campo `id` del DogData
                    dogList.add(it)
                }
            }
            onResult(dogList)
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
            onResult(null)
        }
    }


    fun loadDog(index: Int, onResult: (DogData?) -> Unit) {
        loadAllDog { dogList ->
            if (dogList != null && index in dogList.indices) {
                onResult(dogList[index])
            } else {
                onResult(null)
            }
        }
    }



}
