package com.example.doggo_ourapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
        val newDietRecipe = DietRecipeData(
            idRecipe = recipe.id,
            lastDataDone = null // O imposta una data se necessario
        )

        DietFirebase.saveDietRecipe(newDietRecipe) { success ->
            if (success) {
                Toast.makeText(this, "Ricetta aggiunta alla dieta!", Toast.LENGTH_SHORT).show()
                finish() // Torna indietro o aggiorna UI
            } else {
                Toast.makeText(this, "Errore nell'aggiunta", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
