package com.example.doggo_ourapp

object DogFirebase {

    fun saveDog(dog: DogData,onResult: (Boolean)->Unit) {
        val userId = FirebaseDB.getAuth().currentUser?.uid ?: return
        val dogRef = FirebaseDB.getMDbRef().child("user").child(userId).child("dog").push() // genera un ID univoco
        dog.id = dogRef.key
        dogRef.setValue(dog).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun loadAllDog(onResult: (List<DogData>?) -> Unit) {
        val userId = FirebaseDB.getAuth().currentUser?.uid ?: return
        val dogRef = FirebaseDB.getMDbRef().child("user").child(userId).child("dog")

        dogRef.get().addOnSuccessListener { dataSnapshot ->
            val dogList = mutableListOf<DogData>()
            for (child in dataSnapshot.children) {
                val dog = child.getValue(DogData::class.java)
                dog?.let {
                    it.id = child.key
                    dogList.add(it)
                }
            }
            onResult(dogList)
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
            onResult(null)
        }
    }

    fun selectDog(dogId:String, onResult: (Boolean) -> Unit)
    {
        val userRef = FirebaseDB.getMDbRef().child("user").child(UserFirebase.getCurrentUserId())
        userRef.get().addOnSuccessListener {
            val dogRef = userRef.child("dog").child(dogId)
            dogRef.get().addOnSuccessListener {
                userRef.child("actualDog").setValue(dogId).addOnCompleteListener {
                    onResult(true)
                }
            }.addOnFailureListener {
                it.printStackTrace()
                onResult(false)
            }
        }.addOnFailureListener {
            it.printStackTrace()
            onResult(false)
        }
    }

    fun getActualDog(onResult: (String?) -> Unit) {
        if(UserFirebase.getCurrentUserId()=="")
            onResult(null)
        val actualDog="-OU9JYKmtiQQUxkHcxoW"
        val actualDogRef = FirebaseDB.getMDbRef()
            .child("user")
            .child(UserFirebase.getCurrentUserId())
            .child("actualDog")

        actualDogRef.get()
            .addOnSuccessListener { actualDog ->
                val dogId = actualDog.getValue(String::class.java)
                onResult(dogId)
            }
            .addOnFailureListener {
                it.printStackTrace()
                onResult(null)
            }
    }


    fun loadDog(dogId: String, onResult: (DogData?) -> Unit) {
        val dogRef = FirebaseDB.getMDbRef().child("user").child(UserFirebase.getCurrentUserId()).child("dog").child(dogId)
        dogRef.get().addOnSuccessListener { snapshot ->
            val dog = snapshot.getValue(DogData::class.java)
            onResult(dog)
        }.addOnFailureListener {
            it.printStackTrace()
            onResult(null)
        }
    }

}