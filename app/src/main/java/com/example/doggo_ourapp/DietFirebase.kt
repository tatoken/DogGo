package com.example.doggo_ourapp

object DietFirebase {
    /*fun saveDiet(dogId: String, diet: DietData) {
        val userId = FirebaseDB.getAuth().currentUser?.uid ?: return
        val dietRef = FirebaseDB.getMDbRef()
            .child("user")
            .child(userId)
            .child("dog")
            .child(dogId)
            .child("diet")

        dietRef.setValue(diet)
    }
    fun saveDiet(dogId: String, diet: DietData, onResult: (Boolean) -> Unit) {
        val userId = FirebaseDB.getAuth().currentUser?.uid ?: return
        val dietRef = FirebaseDB.getMDbRef()
            .child("user")
            .child(userId)
            .child("dog")
            .child(dogId)
            .child("diet")

        // diet.id = dietRef.key

        dietRef.setValue(diet).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }*/

    fun saveDiet(diet: DietData, onResult: (Boolean) -> Unit) {
        val userId = FirebaseDB.getAuth().currentUser?.uid
        if (userId == null) {
            onResult(false)
            return
        }

        DogFirebase.getActualDog { dogId ->
            if (dogId == null) {
                onResult(false)
            } else {
                val dietRef = FirebaseDB.getMDbRef()
                    .child("user")
                    .child(userId)
                    .child("dog")
                    .child(dogId)
                    .child("diet")

                dietRef.setValue(diet).addOnCompleteListener { task ->
                    onResult(task.isSuccessful)
                }
            }
        }
    }


    fun loadDiet(onResult: (DietData?) -> Unit) {
        val userId = FirebaseDB.getAuth().currentUser?.uid
        if (userId == null) {
            onResult(null)
            return
        }

        DogFirebase.getActualDog { dogId ->
            if (dogId == null) {
                onResult(null)
            } else {
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
    }

    /*fun saveDietRecipe(dogId: String, dietRecipe: DietRecipeData) {
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
    }*/

    fun saveDietRecipe(dietRecipe: DietRecipeData, onResult: (Boolean) -> Unit) {
        val userId = FirebaseDB.getAuth().currentUser?.uid
        if (userId == null) {
            onResult(false)
            return
        }

        DogFirebase.getActualDog { dogId ->
            if (dogId == null) {
                onResult(false)
            } else {
                val dietRecipeRef = FirebaseDB.getMDbRef()
                    .child("user")
                    .child(userId)
                    .child("dog")
                    .child(dogId)
                    .child("diet")
                    .child("dietRecipe")
                    .push() // crea un ID univoco per ogni dietRecipe

                //dietRecipe.id = dietRecipeRef.key

                dietRecipeRef.setValue(dietRecipe).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                }
            }
        }
    }

    fun loadDietRecipeList(onResult: (List<DietRecipeData>?) -> Unit) {
        val userId = FirebaseDB.getAuth().currentUser?.uid
        if (userId == null) {
            onResult(null)
            return
        }

        DogFirebase.getActualDog { dogId ->
            if (dogId == null) {
                onResult(null)
            } else {
                val dietRecipeRef = FirebaseDB.getMDbRef()
                    .child("user")
                    .child(userId)
                    .child("dog")
                    .child(dogId)
                    .child("diet")
                    .child("dietRecipe")

                dietRecipeRef.get().addOnSuccessListener { dataSnapshot ->
                    val list = mutableListOf<DietRecipeData>()
                    for (child in dataSnapshot.children) {
                        val recipe = child.getValue(DietRecipeData::class.java)
                        recipe?.let { list.add(it) }
                    }
                    onResult(list)
                }.addOnFailureListener {
                    it.printStackTrace()
                    onResult(null)
                }
            }
        }
    }

    fun loadDietRecipe(dietRecipeId: String, onResult: (DietRecipeData?) -> Unit) {
        val userId = FirebaseDB.getAuth().currentUser?.uid
        if (userId == null) {
            onResult(null)
            return
        }

        DogFirebase.getActualDog { dogId ->
            if (dogId == null) {
                onResult(null)
            } else {
                val ref = FirebaseDB.getMDbRef()
                    .child("user")
                    .child(userId)
                    .child("dog")
                    .child(dogId)
                    .child("diet")
                    .child("dietRecipe")
                    .child(dietRecipeId)

                ref.get().addOnSuccessListener { snapshot ->
                    val dietRecipe = snapshot.getValue(DietRecipeData::class.java)
                    onResult(dietRecipe)
                }.addOnFailureListener {
                    it.printStackTrace()
                    onResult(null)
                }
            }
        }
    }

    fun loadAllRecipes(onResult: (List<RecipeData>) -> Unit) {
        val recipeRef = FirebaseDB.getMDbRef().child("recipe")

        recipeRef.get().addOnSuccessListener { dataSnapshot ->
            val recipeList = mutableListOf<RecipeData>()
            for (child in dataSnapshot.children) {
                val recipe = child.getValue(RecipeData::class.java)
                recipe?.let {
                    it.id = child.key // se RecipeData ha un campo `id`, popolarlo
                    recipeList.add(it)
                }
            }
            onResult(recipeList)
        }.addOnFailureListener {
            it.printStackTrace()
            onResult(emptyList())
        }
    }

    fun loadRecipeById(recipeId: String, onResult: (RecipeData?) -> Unit) {
        val recipeRef = FirebaseDB.getMDbRef().child("recipe").child(recipeId)

        recipeRef.get().addOnSuccessListener { dataSnapshot ->
            val recipe = dataSnapshot.getValue(RecipeData::class.java)
            recipe?.id = dataSnapshot.key // opzionale, se hai un campo `id`
            onResult(recipe)
        }.addOnFailureListener {
            it.printStackTrace()
            onResult(null)
        }
    }



}