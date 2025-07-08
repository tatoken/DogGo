package com.example.doggo_ourapp.diet

import android.graphics.Color
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doggo_ourapp.R
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.database.FirebaseDatabase

class Food : Fragment(R.layout.food_layout) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var hBarChart: HorizontalBarChart
    private lateinit var emptyText1: TextView
    private lateinit var emptyText2: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recipeRecyclerView)
        hBarChart = view.findViewById(R.id.hBarChart)
        emptyText1 = view.findViewById(R.id.noDietText1)
        emptyText2 = view.findViewById(R.id.noDietText2)

        recyclerView.visibility = View.GONE
        hBarChart.visibility = View.GONE
        emptyText1.visibility = View.GONE
        emptyText2.visibility = View.GONE

        DietFirebase.checkIfDogHasDiet { hasDiet ->
            if (hasDiet) {
                recyclerView.visibility = View.VISIBLE
                hBarChart.visibility = View.VISIBLE

                loadRecipes()
                loadAndDisplayNutrients()
            } else {
                emptyText1.visibility = View.VISIBLE
                emptyText2.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()

        DietFirebase.clearDietIfNewDay { success ->
            if (success) {
                Log.d("DietReset", "Data dieta aggiornata")
            } else {
                Log.e("DietReset", "Errore o dieta già aggiornata")
            }
        }

        loadRecipes()
        loadAndDisplayNutrients()
    }

    private fun loadRecipes() {
        DietFirebase.loadCompleteRecipesForDiet { recipeList ->
            if (recipeList != null) {
                recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                recyclerView.adapter = RecipeAdapter(recipeList) {
                    val intent = Intent(requireContext(), AddRecipeActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun loadAndDisplayNutrients() {
        DietFirebase.loadTotalNutrientsForDiet { totalMap ->
            DietFirebase.loadMaxNutrientsForDiet { maxMap ->
                if (totalMap != null && maxMap != null) {
                    setupChartWithNutrients(totalMap, maxMap)
                } else {
                    hBarChart.visibility = View.GONE
                }
            }
        }
    }

    private fun setupChartWithNutrients(totalMap: Map<String, Double>, maxMap: Map<String, Double>) {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        val nutrientList = listOf("carbohydrates", "fats", "proteins", "fibers", "vitamins")

        for ((index, nutrient) in nutrientList.withIndex()) {
            val current = totalMap[nutrient]?.toFloat() ?: 0f
            val max = maxMap[nutrient]?.toFloat() ?: 1f
            val percentage = (current / max * 100).coerceAtMost(100f)

            entries.add(BarEntry(index.toFloat(), percentage))
            labels.add(nutrient.replaceFirstChar { it.uppercaseChar() })
        }

        val dataSet = BarDataSet(entries, "Nutrient Intake (%)").apply {
            colors = listOf(
                Color.rgb(239, 83, 80),
                Color.rgb(66, 165, 245),
                Color.rgb(102, 187, 106),
                Color.rgb(255, 202, 40),
                Color.rgb(171, 71, 188)
            )
            valueTextSize = 12f
        }

        hBarChart.apply {
            data = BarData(dataSet).apply {
                barWidth = 0.7f

            }
            description.isEnabled = false
            legend.isEnabled = false

            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 100f
                granularity = 20f
                setDrawGridLines(false)
            }

            axisRight.isEnabled = false

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                granularity = 1f
                textSize = 10f  // Di default è circa 12-14f
                setDrawGridLines(false)
                setDrawAxisLine(false)

                position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            }


            setFitBars(true)
            setVisibleXRangeMaximum(5f) // 👈 importante
            animateY(1000)
            invalidate()
        }
    }




}
