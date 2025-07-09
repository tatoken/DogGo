package com.example.doggo_ourapp.diet

import android.app.AlertDialog
import java.time.LocalDateTime

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doggo_ourapp.R
import java.time.format.DateTimeFormatter

class AddRecipeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AllRecipesAdapter
    private val recipes = mutableListOf<RecipeData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_recipe_from_all)

        recyclerView = findViewById(R.id.recyclerAllRecipes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AllRecipesAdapter(recipes) { selectedRecipe ->
            addRecipeToDogDiet(selectedRecipe)
        }

        recyclerView.adapter = adapter

        loadAllRecipesFromFirebase()
    }

    private fun loadAllRecipesFromFirebase() {
        DietFirebase.loadAllRecipes { loadedRecipes ->
            recipes.clear()
            recipes.addAll(loadedRecipes)
            adapter.notifyDataSetChanged()
        }
    }

    private fun addRecipeToDogDiet(recipe: RecipeData) {
        val currentDateString = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        val newDietRecipe = DietRecipeData(
            idRecipe = recipe.id,
            lastDataDone = currentDateString
        )

        DietFirebase.tryAddRecipeToDiet(recipe) { result ->
            if (!result) {
                saveAndFinish(newDietRecipe)
            }else{
                runOnUiThread {
                    AlertDialog.Builder(this)
                        .setTitle("Warning")
                        .setMessage("This recipe will exceed one or more nutrient limits. Do you want to add it anyway?")
                        .setPositiveButton("Yes") { _, _ ->
                            saveAndFinish(newDietRecipe)
                        }
                        .setNegativeButton("No", null)
                        .show()
                }

            }
        }
    }

    private fun saveAndFinish(dietRecipe: DietRecipeData) {
        DietFirebase.saveDietRecipe(dietRecipe) { success ->
            if (success) {
                Toast.makeText(this, "Recipe added to the diet!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error adding the recipe.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
