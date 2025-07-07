package com.example.doggo_ourapp

object DietFirebase {

    fun saveDiet(diet: DietData, onResult: (Boolean) -> Unit) {

        DogFirebase.getActualDog { dogId ->
            if (dogId == null) {
                onResult(false)
            } else {
                val dietRef = FirebaseDB.getMDbRef()
                    .child("user")
                    .child(UserFirebase.getCurrentUserId())
                    .child("dog")
                    .child(dogId)
                    .child("diet")

                dietRef.setValue(diet).addOnCompleteListener { task ->
                    onResult(true)
                }
            }
        }
    }

    fun loadDiet(onResult: (DietData?) -> Unit) {
        DogFirebase.getActualDog { dogId ->
            if (dogId == null) {
                onResult(null)
            } else {
                val dietRef = FirebaseDB.getMDbRef()
                    .child("user")
                    .child(UserFirebase.getCurrentUserId())
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

    fun saveRecipe(recipe: RecipeData, onResult: (Boolean) -> Unit) {

        val recipeRef = FirebaseDB.getMDbRef()
            .child("recipe")
            .push()

        recipe.id = recipeRef.key

        recipeRef.setValue(recipe).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun loadRecipeById(recipeId: String?, onResult: (RecipeData?) -> Unit) {
        val recipeRef = FirebaseDB.getMDbRef().child("recipe").child(recipeId!!)

        recipeRef.get().addOnSuccessListener { dataSnapshot ->
            val recipe = dataSnapshot.getValue(RecipeData::class.java)
            recipe?.id = dataSnapshot.key // opzionale, se hai un campo `id`
            onResult(recipe)
        }.addOnFailureListener {
            it.printStackTrace()
            onResult(null)
        }
    }


    fun saveDietRecipe(dietRecipe: DietRecipeData, onResult: (Boolean) -> Unit) {
        DogFirebase.getActualDog { dogId ->
            if (dogId == null) {
                onResult(false)
            } else {
                val recipeId = dietRecipe.idRecipe
                if (recipeId.isNullOrEmpty()) {
                    onResult(false)
                } else {
                    val recipeRef = FirebaseDB.getMDbRef()
                        .child("recipe")
                        .child(recipeId)

                    recipeRef.get().addOnSuccessListener { snapshot ->
                        if (snapshot.exists()) {
                            val dietRecipeRef = FirebaseDB.getMDbRef()
                                .child("user")
                                .child(UserFirebase.getCurrentUserId())
                                .child("dog")
                                .child(dogId)
                                .child("diet")
                                .child("dietRecipe")
                                .push()

                            dietRecipe.id = dietRecipeRef.key

                            dietRecipeRef.setValue(dietRecipe).addOnCompleteListener { task ->
                                onResult(task.isSuccessful)
                            }
                        } else {
                            onResult(false)
                        }
                    }.addOnFailureListener {
                        onResult(false)
                    }
                }
            }
        }
    }

    fun loadDietRecipeList(onResult: (List<DietRecipeData>?) -> Unit) {

        DogFirebase.getActualDog { dogId ->
            if (dogId == null) {
                onResult(null)
            } else {
                val dietRecipeRef = FirebaseDB.getMDbRef()
                    .child("user")
                    .child(UserFirebase.getCurrentUserId())
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
        DogFirebase.getActualDog { dogId ->
            if (dogId == null) {
                onResult(null)
            } else {
                val ref = FirebaseDB.getMDbRef()
                    .child("user")
                    .child(UserFirebase.getCurrentUserId())
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
                    it.id = child.key
                    recipeList.add(it)
                }
            }
            onResult(recipeList)
        }.addOnFailureListener {
            it.printStackTrace()
            onResult(emptyList())
        }
    }

    fun loadCompleteRecipesForDiet(onResult: (List<RecipeData>?) -> Unit) {
        loadDietRecipeList { dietRecipeList ->
            if (dietRecipeList.isNullOrEmpty()) {
                onResult(emptyList())
                return@loadDietRecipeList
            }

            val recipes = mutableListOf<RecipeData>()
            var loadedCount = 0

            for (dietRecipe in dietRecipeList) {
                loadRecipeById(dietRecipe.idRecipe) { recipe ->
                    if (recipe != null) {
                        recipes.add(recipe)
                    }
                    loadedCount++

                    if (loadedCount == dietRecipeList.size) {
                        onResult(recipes)
                    }
                }
            }
        }
    }


}