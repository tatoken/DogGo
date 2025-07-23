package com.example.doggo_ourapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.doggo_ourapp.diet.DietFirebase
import com.example.doggo_ourapp.diet.Food
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Homepage : Fragment(R.layout.homepage_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGymSection(view)

        setupEventSection(view)

        setupFoodSection(view)

        setupChallengesSection(view)
    }

    //Gym
    private fun setupGymSection(view: View) {
        val lineChart = view.findViewById<LineChart>(R.id.lineChart)
        val barChart = view.findViewById<BarChart>(R.id.barChart)
        val day = 30

        TrainingFirebase.loadTrainingsOfLastDays(day) { trainingList ->

            if (trainingList != null) {
                val kmData = generateKmPerDayData(trainingList, day)
                setupLineChart(lineChart, kmData)
            }
        }
    }

    fun generateKmPerDayData(trainingList: List<TrainingData>, days: Int): Map<String, Float> {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val today = LocalDate.now()

        val kmMap = mutableMapOf<String, Float>()
        for (i in days downTo 0) {
            val date = today.minusDays(i.toLong())
            kmMap[date.format(formatter)] = 0f
        }

        for (training in trainingList) {
            val date = training.date
            val km = training.km?.toFloatOrNull() ?: 0f
            if (date != null && kmMap.containsKey(date)) {
                kmMap[date] = kmMap[date]!! + km
            }
        }

        return kmMap
    }

    private fun setupLineChart(chart: LineChart, kmPerDay: Map<String, Float>) {
        val entries = mutableListOf<Entry>()
        val labels = mutableListOf<String>()

        var index = 0f

        for ((date, km) in kmPerDay.entries) {
            entries.add(Entry(index, km))
            labels.add(date.substring(0,5))
            index++
        }

        val dataSet = LineDataSet(entries, "Daily Km")
        dataSet.color = Color.rgb(170, 0, 255)
        dataSet.setDrawFilled(true)
        dataSet.setDrawCircles(true)
        dataSet.setDrawValues(false)
        dataSet.lineWidth = 2f
        dataSet.fillColor = Color.rgb(143, 0, 255)

        val legend = chart.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        legend.textSize = 12f


        chart.data = LineData(dataSet)
        chart.description.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.setTouchEnabled(true)
        chart.setPinchZoom(false)
        chart.setExtraOffsets(10f, 10f, 10f, 30f)

        // X Axis
        val xAxis = chart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.labelRotationAngle = -25f
        xAxis.granularity = 1f
        xAxis.labelCount = 5 // Mostra meno date per evitare sovrapposizione

        chart.axisLeft.axisMinimum = 0f
        chart.animateY(1000)
        chart.invalidate()
    }

    //Event
    private fun setupEventSection(view: View) {

    }

    //Food
    private fun setupFoodSection(view: View) {
        val foodSection = view.findViewById<View>(R.id.foodSection)
        val recipeContainer = view.findViewById<LinearLayout>(R.id.recipeContainer)

        // Clic per navigare al fragment Food
        foodSection.setOnClickListener {
            (activity as? MainApp)?.replaceFragment(Food())
        }

        // Caricamento ricette del giorno
        DietFirebase.loadCompleteRecipesForDiet { recipeList ->
            if (recipeList.isNullOrEmpty()) {
                Log.d("Homepage", "Nessuna ricetta trovata.")
                return@loadCompleteRecipesForDiet
            }

            val inflater = layoutInflater

            for (recipe in recipeList) {
                val itemView = inflater.inflate(R.layout.homepage_recipe_item, recipeContainer, false)

                val imageView = itemView.findViewById<ImageView>(R.id.recipeImage)
                val titleView = itemView.findViewById<TextView>(R.id.recipeTitle)

                titleView.text = recipe.name

                lifecycleScope.launch {
                    val bitmap = SupabaseManager.downloadImage("recipe", recipe.id!! + ".png")
                    imageView?.setImageBitmap(bitmap)
                }

                recipeContainer.addView(itemView)
            }
        }
    }

    //Challenge
    private fun setupChallengesSection(view: View) {

    }
}
