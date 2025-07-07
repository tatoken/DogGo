package com.example.doggo_ourapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class AllRecipesAdapter(
    private val recipes: List<RecipeData>,
    private val onRecipeSelected: (RecipeData) -> Unit
) : RecyclerView.Adapter<AllRecipesAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.recipeTitle)
        private val time: TextView = itemView.findViewById(R.id.recipeTime)
        private val difficulty: TextView = itemView.findViewById(R.id.recipeDifficulty)
        private val cost: TextView = itemView.findViewById(R.id.recipeCost)
        private val description: TextView = itemView.findViewById(R.id.recipeDescription)
        private val carbs: TextView = itemView.findViewById(R.id.carbohydratesValue)
        private val fats: TextView = itemView.findViewById(R.id.fatsValue)
        private val proteins: TextView = itemView.findViewById(R.id.proteinsValue)
        private val fibers: TextView = itemView.findViewById(R.id.fibersValue)
        private val vitamins: TextView = itemView.findViewById(R.id.vitaminsValue)
        private val imageCard: CardView = itemView.findViewById(R.id.recipePhoto)

        fun bind(recipe: RecipeData, onClick: (RecipeData) -> Unit) {
            title.text = recipe.name
            time.text = "TIME: ${recipe.duration}"
            difficulty.text = "DIFFICULTY: ${recipe.difficulty}"
            cost.text = "COST: ${recipe.cost}"
            description.text = recipe.description
            carbs.text = "Carbohydrates: ${recipe.carbohydrates}"
            fats.text = "Fats: ${recipe.fats}"
            proteins.text = "Proteins: ${recipe.proteins}"
            fibers.text = "Fibers: ${recipe.fibers}"
            vitamins.text = "Vitamins: ${recipe.vitamins}"

            val imageView = imageCard.getChildAt(0) as? ImageView
            imageView?.setImageResource(R.drawable.receipe) // o Glide per immagini remote

            itemView.setOnClickListener {
                onClick(recipe)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_card, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position], onRecipeSelected)
    }

    override fun getItemCount(): Int = recipes.size
}
