package com.example.doggo_ourapp.diet

import android.graphics.Color
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.Button
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
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.database.FirebaseDatabase

class Food : Fragment(R.layout.food_layout) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var hBarChart: HorizontalBarChart
    private lateinit var insertDietBtn1: Button
    private lateinit var insertDietBtn2: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recipeRecyclerView)
        hBarChart = view.findViewById(R.id.hBarChart)
        insertDietBtn1 = view.findViewById(R.id.btnInsertDiet1)
        insertDietBtn2 = view.findViewById(R.id.btnInsertDiet2)

        recyclerView.visibility = View.GONE
        hBarChart.visibility = View.GONE
        insertDietBtn1.visibility = View.GONE
        insertDietBtn2.visibility = View.GONE

        DietFirebase.checkIfDogHasDiet { hasDiet ->
            if (hasDiet) {
                recyclerView.visibility = View.VISIBLE
                hBarChart.visibility = View.VISIBLE

                loadRecipes()
                loadAndDisplayNutrients()
            } else {
                insertDietBtn1.visibility = View.VISIBLE
                insertDietBtn2.visibility = View.VISIBLE
                insertDietBtn1.setBackgroundResource(R.drawable.button_black_bg)
                insertDietBtn2.setBackgroundResource(R.drawable.button_black_bg)

                insertDietBtn1.setOnClickListener {
                    val intent = Intent(requireContext(), AddDiet::class.java)
                    startActivity(intent)
                }

                insertDietBtn2.setOnClickListener {
                    val intent = Intent(requireContext(), AddDiet::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        DietFirebase.checkIfDogHasDiet { hasDiet ->
            if (hasDiet) {
                DietFirebase.clearDietIfNewDay { _ ->

                    recyclerView.visibility = View.VISIBLE
                    hBarChart.visibility = View.VISIBLE
                    insertDietBtn1.visibility = View.GONE
                    insertDietBtn2.visibility = View.GONE

                    loadRecipes()
                    loadAndDisplayNutrients()
                }
            } else {
                recyclerView.visibility = View.GONE
                hBarChart.visibility = View.GONE
                insertDietBtn1.visibility = View.VISIBLE
                insertDietBtn2.visibility = View.VISIBLE
            }
        }
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
            valueFormatter = object : ValueFormatter() {
                override fun getBarLabel(barEntry: BarEntry): String {
                    return String.format("%.1f%%", barEntry.y)
                }
            }
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
