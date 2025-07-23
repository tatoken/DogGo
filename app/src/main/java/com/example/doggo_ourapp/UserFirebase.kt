package com.example.doggo_ourapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate

object UserFirebase {
    fun signup(name: String, surname: String, birthDate: String, bio: String, email: String, password: String, onResult: (Boolean) -> Unit) {
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

    private fun addUserToDatabase(name: String, surname:String, birthDate: String, bio:String, email: String, uid: String) {
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

    fun setPhotoOfUser(photo: String) {
        val dbRef = FirebaseDB.getMDbRef()
        val userRef = dbRef.child("user").child(getCurrentUserId())

        userRef.child("photo").setValue(photo)
    }

    fun getUserByUid(uid: String, onResult: (UserData?) -> Unit) {
        val dbRef = FirebaseDB.getMDbRef()
        val userRef = dbRef.child("user").child(uid)

        userRef.get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(UserData::class.java)
            onResult(user)
        }.addOnFailureListener {
            it.printStackTrace()
            onResult(null)
        }
    }



    fun getCurrentUserId():String
    {
        return FirebaseDB.getAuth().currentUser?.uid ?: return ""
    }

    fun getCurrentUser(onResult: (UserData?) -> Unit) {
        val dbRef = FirebaseDB.getMDbRef()
        val userRef = dbRef.child("user").child(getCurrentUserId())

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserData::class.java)
                onResult(user)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(null)
            }
        })
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

    fun getCurrentUserTotalPoints(onResult: (String?) -> Unit)
    {
        val dbRef = FirebaseDB.getMDbRef()
        val userRef = dbRef.child("user").child(getCurrentUserId())

        userRef.child("totalPoints").get().addOnSuccessListener { snapshotPoints ->
            val pointsStr = snapshotPoints.getValue(String::class.java)
            onResult(pointsStr)
        }
    }

    fun loadTopUsers(number:Int,onResult: (List<UserData>?) -> Unit) {
        val userRef = FirebaseDB.getMDbRef().child("user")

        userRef.get().addOnSuccessListener { snapshot ->
            val userList = mutableListOf<UserData>()

            for (child in snapshot.children) {
                val user = child.getValue(UserData::class.java)
                user?.let {
                    it.uid = child.key
                    userList.add(it)
                }
            }

            val sortedUsers = userList.sortedByDescending { it.totalPoints }.take(number)
            onResult(sortedUsers)
        }.addOnFailureListener {
            it.printStackTrace()
            onResult(null)
        }
    }


}