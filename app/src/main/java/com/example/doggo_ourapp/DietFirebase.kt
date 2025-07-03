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

    fun saveDietRecipe(dogId: String, dietRecipe: DietRecipeData) {
        val userId = FirebaseDB.getAuth().currentUser?.uid ?: return
        val dietRecipeRef = FirebaseDB.getMDbRef()
            .child("user")
            .child(userId)
            .child("dog")
            .child(dogId)
            .child("diet")
            .child("dietRecipe")
            .push() // crea un ID univoco per ogni dietRecipe

        dietRecipeRef.setValue(dietRecipe)
    }

    fun loadDietRecipeList(dogId: String, onResult: (List<DietRecipeData>?) -> Unit) {
        val userId = FirebaseDB.getAuth().currentUser?.uid ?: return
        val dietRecipeRef = FirebaseDB.getMDbRef()
            .child("user")
            .child(userId)
            .child("dog")
            .child(dogId)
            .child("diet")
            .child("dietRecipe")

        dietRecipeRef.get().addOnSuccessListener { snapshot ->
            val list = mutableListOf<DietRecipeData>()
            for (child in snapshot.children) {
                val recipe = child.getValue(DietRecipeData::class.java)
                recipe?.let { list.add(it) }
            }
            onResult(list)
        }.addOnFailureListener {
            it.printStackTrace()
            onResult(null)
        }
    }

    fun loadDietRecipe(dogId: String, index: Int, onResult: (DietRecipeData?) -> Unit) {
        loadDietRecipeList(dogId) { dietList ->
            if (dietList != null && index in dietList.indices) {
                onResult(dietList[index])
            } else {
                onResult(null)
            }
        }
    }


}