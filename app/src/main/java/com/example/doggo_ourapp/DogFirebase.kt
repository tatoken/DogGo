package com.example.doggo_ourapp

object DogFirebase {
    fun saveDog(dog: DogData) {
        val userId = FirebaseDB.getAuth().currentUser?.uid ?: return
        val dogRef = FirebaseDB.getMDbRef().child("user").child(userId).child("dog").push() // genera un ID univoco
        dog.id = dogRef.key
        dogRef.setValue(dog)
    }


    fun loadAllDog(onResult: (List<DogData>?) -> Unit) {
        val userId = FirebaseDB.getAuth().currentUser?.uid ?: return
        val dogRef = FirebaseDB.getMDbRef().child("user").child(userId).child("dog")

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