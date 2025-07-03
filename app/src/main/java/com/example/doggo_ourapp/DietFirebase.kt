package com.example.doggo_ourapp

object DietFirebase {
    fun saveDiet(dogId: String, diet: DietData) {
        val userId = FirebaseDB.getAuth().currentUser?.uid ?: return
        val dietRef = FirebaseDB.getMDbRef()
            .child("user")
            .child(userId)
            .child("dog")
            .child(dogId)
            .child("diet")

        dietRef.setValue(diet)
    }

    fun loadDiet(dogId: String, onResult: (DietData?) -> Unit) {
        val userId = FirebaseDB.getAuth().currentUser?.uid ?: return
        val dietRef = FirebaseDB.getMDbRef()
            .child("user")
            .child(userId)
            .child("dog")
            .child(dogId)
            .child("diet")

        dietRef.get().addOnSuccessListener { dataSnapshot ->
            val diet = dataSnapshot.getValue(DietData::class.java)
            onResult(diet)
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
            onResult(null)
        }
    }

}