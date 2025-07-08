package com.example.doggo_ourapp.diet

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.doggo_ourapp.R

class RecipeDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_detail)

        val recipe = DietFirebase.selectedRecipe

        if (recipe != null) {
            findViewById<TextView>(R.id.recipeTitle).text = recipe.name
            findViewById<TextView>(R.id.recipeTime).text = "TIME: ${recipe.duration}"
            findViewById<TextView>(R.id.recipeDifficulty).text = "DIFFICULTY: ${recipe.difficulty}"
            findViewById<TextView>(R.id.recipeCost).text = "COST: ${recipe.cost}"
            findViewById<TextView>(R.id.recipeDescription).text = recipe.description

            findViewById<TextView>(R.id.carbohydratesValue).text = "Carbohydrates: ${recipe.carbohydrates}"
            findViewById<TextView>(R.id.fatsValue).text = "Fats: ${recipe.fats}"
            findViewById<TextView>(R.id.proteinsValue).text = "Proteins: ${recipe.proteins}"
            findViewById<TextView>(R.id.fibersValue).text = "Fibers: ${recipe.fibers}"
            findViewById<TextView>(R.id.vitaminsValue).text = "Vitamins: ${recipe.vitamins}"

            val imageView = findViewById<ImageView>(R.id.recipeImage)
            imageView.setImageResource(R.drawable.receipe) // o usa Glide se usi URL remoto
        } else {
            // Se per qualche motivo la ricetta è nulla, torna indietro
            finish()
        }
    }
}
