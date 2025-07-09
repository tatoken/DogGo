package com.example.doggo_ourapp.diet

import android.util.Log
import com.example.doggo_ourapp.DogFirebase
import com.example.doggo_ourapp.FirebaseDB
import com.example.doggo_ourapp.UserFirebase
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate

object DietFirebase {

    var selectedRecipe: RecipeData? = null

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

    fun loadRecipe(recipeId: String?, onResult: (RecipeData?) -> Unit) {
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


    fun loadCompleteRecipesForDiet(onResult: (List<RecipeData>?) -> Unit) {
        loadDietRecipeList { dietRecipeList ->
            if (dietRecipeList.isNullOrEmpty()) {
                onResult(emptyList())
                return@loadDietRecipeList
            }

            val recipes = mutableListOf<RecipeData>()
            var loadedCount = 0

            for (dietRecipe in dietRecipeList) {
                loadRecipe(dietRecipe.idRecipe) { recipe ->
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

    /**
     * Ogni ricetta associata al cane a dei nutrienti, il metodo calcola la somma di
     * ogni categoria di nutrienti e la restituisce come una mappa
     */
    fun loadTotalNutrientsForDiet(onResult: (Map<String, Double>) -> Unit) {
        loadCompleteRecipesForDiet { recipeList ->
            if (recipeList.isNullOrEmpty()) {
                onResult(mapOf(
                    "carbohydrates" to 0.0,
                    "fats" to 0.0,
                    "proteins" to 0.0,
                    "fibers" to 0.0,
                    "vitamins" to 0.0
                ))
                return@loadCompleteRecipesForDiet
            }

            var totalCarbohydrates = 0.0
            var totalFats = 0.0
            var totalProteins = 0.0
            var totalFibers = 0.0
            var totalVitamins = 0.0

            recipeList.forEach { recipe ->
                totalCarbohydrates += recipe.carbohydrates?.toDoubleOrNull() ?: 0.0
                totalFats += recipe.fats?.toDoubleOrNull() ?: 0.0
                totalProteins += recipe.proteins?.toDoubleOrNull() ?: 0.0
                totalFibers += recipe.fibers?.toDoubleOrNull() ?: 0.0
                totalVitamins += recipe.vitamins?.toDoubleOrNull() ?: 0.0
            }

            onResult(mapOf(
                "carbohydrates" to totalCarbohydrates,
                "fats" to totalFats,
                "proteins" to totalProteins,
                "fibers" to totalFibers,
                "vitamins" to totalVitamins
            ))
        }
    }

    /**
     * Ogni cane ha una dieta con dei valori massimi per ogni nutrienti,
     * il metodo lì restituisce
     */
    fun loadMaxNutrientsForDiet(onResult: (Map<String, Double>?) -> Unit) {
        loadDiet { diet ->
            if (diet != null) {
                val nutrientMap = mapOf(
                    "carbohydrates" to diet.carbohydrates?.toDoubleOrNull(),
                    "fats" to diet.fats?.toDoubleOrNull(),
                    "proteins" to diet.proteins?.toDoubleOrNull(),
                    "fibers" to diet.fibers?.toDoubleOrNull(),
                    "vitamins" to diet.vitamins?.toDoubleOrNull()
                ).filterValues { it != null } as Map<String, Double>
                onResult(nutrientMap)
            } else {
                onResult(null)
            }
        }
    }

    fun checkIfDogHasDiet(onResult: (Boolean) -> Unit) {
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

                dietRef.get()
                    .addOnSuccessListener { snapshot ->
                        val hasDiet = snapshot.exists() && snapshot.childrenCount > 0
                        onResult(hasDiet)
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                        onResult(false)
                    }
            }
        }
    }

    fun clearDietIfNewDay(onResult: (Boolean) -> Unit) {
        val today = LocalDate.now().toString() // "YYYY-MM-DD"

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

                dietRef.get().addOnSuccessListener { snapshot ->
                    val lastCleared = snapshot.child("lastCleared").getValue(String::class.java)

                    if (lastCleared != today) {
                        // Cancella dietRecipe
                        dietRef.child("dietRecipe").removeValue()
                        // Aggiorna la data di ultimo svuotamento
                        dietRef.child("lastCleared").setValue(today)
                        Log.d("DietReset", "Dieta svuotata perché è un nuovo giorno")
                    } else {
                        onResult(false)
                        Log.d("DietReset", "Dieta già svuotata oggi")
                    }
                }.addOnFailureListener {
                    Log.e("DietReset", "Errore nel recupero della dieta", it)
                }
            }
        }
    }

    fun tryAddRecipeToDiet(
        recipe: RecipeData,
        onResult: (Boolean) -> Unit
    ) {
        loadTotalNutrientsForDiet { totalMap ->
            loadMaxNutrientsForDiet { maxMapNullable ->
                val maxMap = maxMapNullable ?: return@loadMaxNutrientsForDiet

                val nutrientMap = mapOf(
                    "carbohydrates" to recipe.carbohydrates?.toDoubleOrNull(),
                    "fats" to recipe.fats?.toDoubleOrNull(),
                    "proteins" to recipe.proteins?.toDoubleOrNull(),
                    "fibers" to recipe.fibers?.toDoubleOrNull(),
                    "vitamins" to recipe.vitamins?.toDoubleOrNull()
                ).filterValues { it != null } as Map<String, Double>

                val exceeds = nutrientMap.any { (key, value) ->
                    val current = totalMap[key] ?: 0.0
                    val max = maxMap[key] ?: Double.MAX_VALUE
                    Log.d("DietDebug", "Nutrients exceeded: $current,  $value")
                    (current + value) > max
                }
                Log.d("DietDebug", "Nutrients exceeded: $exceeds")

                onResult(exceeds)
            }
        }
    }
}