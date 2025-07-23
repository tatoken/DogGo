package com.example.doggo_ourapp.diet

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.doggo_ourapp.R
import com.example.doggo_ourapp.SupabaseManager
import kotlinx.coroutines.launch

class RecipeDetail : AppCompatActivity() {

    private lateinit var deleteButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_detail)

        val recipe = DietFirebase.selectedRecipe

        if (recipe != null) {

            deleteButton = findViewById<ImageButton>(R.id.btnDeleteRecipe)

            deleteButton.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this recipe?")
                    .setPositiveButton("Delete") { _, _ ->
                        DietFirebase.deleteDietRecipe(recipe.id!!) { success ->
                            runOnUiThread {
                                if (success) {
                                    Toast.makeText(this, "Recipe deleted successfully", Toast.LENGTH_SHORT).show()
                                    finish()
                                } else {
                                    Toast.makeText(this, "Failed to delete recipe", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }


            findViewById<TextView>(R.id.recipeTitle).text = recipe.name
            findViewById<TextView>(R.id.recipeTime).text = "TIME:\n${recipe.duration}"
            findViewById<TextView>(R.id.recipeDifficulty).text = "DIFFICULTY:\n${recipe.difficulty}"
            findViewById<TextView>(R.id.recipeCost).text = "COST:\n${recipe.cost}"
            findViewById<TextView>(R.id.recipeDescription).text = recipe.description

            findViewById<TextView>(R.id.carbohydratesValue).text = "Carbohydrates: ${recipe.carbohydrates}"
            findViewById<TextView>(R.id.fatsValue).text = "Fats: ${recipe.fats}"
            findViewById<TextView>(R.id.proteinsValue).text = "Proteins: ${recipe.proteins}"
            findViewById<TextView>(R.id.fibersValue).text = "Fibers: ${recipe.fibers}"
            findViewById<TextView>(R.id.vitaminsValue).text = "Vitamins: ${recipe.vitamins}"

            val imageView = findViewById<ImageView>(R.id.recipeImage)

            lifecycleScope.launch {
                val bitmap= SupabaseManager.downloadImage("recipe", recipe.id!!+".png")
                imageView?.setImageBitmap(bitmap)
            }

        } else {
            // Se per qualche motivo la ricetta è nulla, torna indietro
            finish()
        }
    }
}
