package com.example.doggo_ourapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate

object FirebaseDB {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDbRef: DatabaseReference = FirebaseDatabase.getInstance("https://doggo-6c19f-default-rtdb.europe-west1.firebasedatabase.app").getReference()

    fun getMDbRef (): DatabaseReference{
        return mDbRef
    }

    fun getAuth (): FirebaseAuth{
        return auth
    }

}
