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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        val challengeSection = view.findViewById<View>(R.id.chartSection)
        val lineChart = view.findViewById<LineChart>(R.id.lineChart)
        val barChart = view.findViewById<BarChart>(R.id.barChart)
        val day = 30

        // Clic per navigare al fragment Gym
        challengeSection.setOnClickListener {
            (activity as? MainApp)?.replaceFragment(Gym())
        }

        TrainingFirebase.loadTrainingsOfLastDays(day) { trainingList ->
            // Controllo ciclo di vita per evitare crash se fragment non è più attivo
            if (!isAdded || view == null) return@loadTrainingsOfLastDays

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
        val eventSection = view.findViewById<View>(R.id.eventSection)
        val calendarContainer = view.findViewById<LinearLayout>(R.id.calendarSectionInnerLayout)

        // Clic per navigare al fragment Calendar
        eventSection.setOnClickListener {
            (activity as? MainApp)?.replaceFragment(Calendar())
        }

        val today = LocalDate.now().toString() // formato yyyy-MM-dd

        EventFirebase.loadEventsByDate(today) { events ->
            // Controllo importante per evitare crash se il fragment non è più attivo
            if (!isAdded || view == null) return@loadEventsByDate

            calendarContainer.removeAllViews() // Pulisce prima di aggiungere, evita duplicati

            if (events.isEmpty()) {
                val emptyText = TextView(requireContext()).apply {
                    text = "Nothing to do today"
                    textSize = 14f
                    setTextColor(Color.GRAY)
                }
                calendarContainer.addView(emptyText)
                return@loadEventsByDate
            }

            events.sortedBy { it.time }.forEach { event ->
                val itemView = layoutInflater.inflate(R.layout.homepage_event_item, calendarContainer, false)

                itemView.findViewById<TextView>(R.id.eventTime).text = event.time
                itemView.findViewById<TextView>(R.id.eventName).text = event.title

                calendarContainer.addView(itemView)

                val divider = View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 2
                    )
                    setBackgroundColor(Color.parseColor("#CCCCCC"))
                }
                calendarContainer.addView(divider)
            }
        }
    }




    private fun setupFoodSection(view: View) {
        val foodSection = view.findViewById<View>(R.id.foodSection)
        val recipeContainer = view.findViewById<LinearLayout>(R.id.recipeContainer)

        // Clic per navigare al fragment Food
        foodSection.setOnClickListener {
            (activity as? MainApp)?.replaceFragment(Food())
        }

        DietFirebase.loadCompleteRecipesForDiet { recipeList ->
            // Controllo di sicurezza per evitare crash se fragment non più attivo
            if (!isAdded || view == null) return@loadCompleteRecipesForDiet

            // Pulisce il contenitore prima di aggiungere nuove view
            recipeContainer.removeAllViews()

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

                viewLifecycleOwner.lifecycleScope.launch {
                    val bitmap = SupabaseManager.downloadImage("recipe", recipe.id!! + ".png")
                    // Controllo extra nel caso la view venga distrutta durante il caricamento
                    if (isAdded && bitmap != null) {
                        imageView?.setImageBitmap(bitmap)
                    }
                }

                recipeContainer.addView(itemView)
            }
        }
    }


    private fun setupChallengesSection(view: View) {
        val challengeSection = view.findViewById<View>(R.id.trophySection)
        val challengeRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewBadge)

        // Clic per navigare al fragment Trophy
        challengeSection.setOnClickListener {
            (activity as? MainApp)?.replaceFragment(Trophy())
        }

        BadgeFirebase.loadAllBadges { badges ->
            // Controllo ciclo di vita per evitare crash se fragment non più attivo
            if (!isAdded || view == null) return@loadAllBadges

            if (!badges.isNullOrEmpty()) {
                val previewBadges = badges.take(4)

                val adapter = BadgeAdapter(previewBadges, viewLifecycleOwner.lifecycleScope, compactMode = true)
                challengeRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                challengeRecyclerView.adapter = adapter
            } else {
                println("No challenge has already started")
            }
        }
    }


}
