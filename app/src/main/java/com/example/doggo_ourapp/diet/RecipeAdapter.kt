package com.example.doggo_ourapp.diet

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.doggo_ourapp.R
import com.example.doggo_ourapp.SupabaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class RecipeAdapter(
    private val recipes: List<RecipeData>,
    private val scope: CoroutineScope,
    private val onAddClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int = recipes.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position < recipes.size) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == 0) {
            val view = inflater.inflate(R.layout.recipe_card, parent, false)
            RecipeViewHolder(view, scope) // 👈 passa il `scope` qui
        } else {
            val view = inflater.inflate(R.layout.add_recipe_card, parent, false)
            AddViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecipeViewHolder && position < recipes.size) {
            holder.bind(recipes[position])
        } else if (holder is AddViewHolder) {
            holder.itemView.setOnClickListener { onAddClicked() }
        }
    }

    class RecipeViewHolder(itemView: View, private val scope: CoroutineScope) : RecyclerView.ViewHolder(itemView) {
        fun bind(recipe: RecipeData) {
            itemView.findViewById<TextView>(R.id.recipeTitle).text = recipe.name
            itemView.findViewById<TextView>(R.id.recipeTime).text = "TIME:\n${recipe.duration}"
            itemView.findViewById<TextView>(R.id.recipeDifficulty).text = "DIFFICULTY:\n${recipe.difficulty}"
            itemView.findViewById<TextView>(R.id.recipeCost).text = "COST:\n${recipe.cost}"

            itemView.findViewById<TextView>(R.id.carbohydratesValue).text = "Carbohydrates: ${recipe.carbohydrates}"
            itemView.findViewById<TextView>(R.id.fatsValue).text = "Fats: ${recipe.fats}"
            itemView.findViewById<TextView>(R.id.proteinsValue).text = "Proteins: ${recipe.proteins}"
            itemView.findViewById<TextView>(R.id.fibersValue).text = "Fibers: ${recipe.fibers}"
            itemView.findViewById<TextView>(R.id.vitaminsValue).text = "Vitamins: ${recipe.vitamins}"

            val imageCard = itemView.findViewById<CardView>(R.id.recipePhoto)
            val imageView = imageCard.getChildAt(0) as? ImageView

            scope.launch {
                val bitmap=SupabaseManager.downloadImage("recipe", recipe.id!!+".png")
                imageView?.setImageBitmap(bitmap)
            }

            itemView.setOnClickListener {
                DietFirebase.selectedRecipe = recipe
                val context = itemView.context
                val intent = Intent(context, RecipeDetail::class.java)
                context.startActivity(intent)
            }
        }
    }

    class AddViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
