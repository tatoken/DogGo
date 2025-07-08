package com.example.doggo_ourapp

import android.graphics.Color
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class Food : Fragment(R.layout.food_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //startActivity(Intent(requireContext(), PieChartActivity::class.java))

        val recyclerView = view.findViewById<RecyclerView>(R.id.recipeRecyclerView)

        DietFirebase.loadCompleteRecipesForDiet { recipeList ->
            if (recipeList != null) {
                recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                recyclerView.adapter = RecipeAdapter(recipeList) {
                    val intent = Intent(requireContext(), AddRecipeActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        val pieChart = view.findViewById<PieChart>(R.id.pieChart)

        // Carica dati per il grafico
        DietFirebase.loadTotalNutrientsForDiet { nutrientMap ->
            val entries = nutrientMap.map { (label, value) ->
                PieEntry(value.toFloat(), label.capitalize())
            }.filter { it.value > 0f }

            val dataSet = PieDataSet(entries, "")
            val colors = listOf(
                Color.rgb(239, 83, 80),     // Red 400
                Color.rgb(66, 165, 245),    // Blue 400
                Color.rgb(102, 187, 106),   // Green 400
                Color.rgb(255, 238, 88),    // Yellow 400
                Color.rgb(171, 71, 188)     // Purple 400
            )
            dataSet.colors = colors
            //dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
            dataSet.valueTextSize = 14f
            dataSet.valueTextColor = Color.WHITE
            dataSet.sliceSpace = 2f

            val data = PieData(dataSet)

            pieChart.apply {
                this.data = data
                description.isEnabled = false

                // Legenda a destra
                legend.isEnabled = true
                legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                legend.orientation = Legend.LegendOrientation.VERTICAL
                legend.setDrawInside(false)

                // Solo numeri nel grafico
                setDrawEntryLabels(false)

                // Anima il grafico
                animateY(1000)
                invalidate()
            }
        }
    }
}
