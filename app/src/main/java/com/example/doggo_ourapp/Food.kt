package com.example.doggo_ourapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Food : Fragment(R.layout.food_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recipeRecyclerView)

        DietFirebase.loadDietRecipeList { dietList ->
            if (dietList != null) {
                val recipeList = mutableListOf<RecipeData>()
                val pendingCount = dietList.size

                if (pendingCount == 0) return@loadDietRecipeList

                var loadedCount = 0
                dietList.forEach { diet ->
                    DietFirebase.loadRecipeById(diet.idRecipe) { recipe ->
                        recipe?.let { recipeList.add(it) }
                        loadedCount++
                        if (loadedCount == pendingCount) {
                            recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                            recyclerView.adapter = RecipeAdapter(recipeList) {
                                val intent = Intent(requireContext(), AddRecipeActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
    }

}
