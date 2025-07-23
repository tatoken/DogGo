package com.example.doggo_ourapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Gym : Fragment(R.layout.training_layout) {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrainingAdapter

    private lateinit var noTrainingText:LinearLayout

    private lateinit var lineChart:LineChart
    private lateinit var ourView:View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        noTrainingText=view.findViewById(R.id.noTrainingText)

        lineChart = view.findViewById(R.id.lineChart)
        ourView=view

        refreshData()

        view.findViewById<FloatingActionButton>(R.id.btn_start).setOnClickListener {
            if (areLocationPermissionsGranted()) {
                try {
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            val intent = Intent(requireContext(), TrackingActivity::class.java)
                            trainingResultLauncher.launch(intent)

                        } else {
                            Toast.makeText(requireContext(), "Enable position", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error to access position: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Security error: ${e.message}", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Generic error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                requestLocationPermissions()
            }
        }

    }

    private fun areLocationPermissionsGranted(): Boolean {
        val context = requireContext()
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permits granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Required location permits", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val trainingResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshData()
        }
    }

    private fun refreshData() {
        TrainingFirebase.loadTrainingsOfLastDays(7) { trainingList ->
            if (trainingList != null) {
                val kmData = generateKmPerDayData(trainingList, 7)
                setupLineChart(lineChart, kmData)
            }
        }

        recyclerView = ourView.findViewById(R.id.recyclerViewTraining)
        TrainingFirebase.loadTrainingsOfLastDays(1) { trainings ->

            if (trainings.isEmpty()) {
                noTrainingText.visibility=View.VISIBLE
            }
            else {
                noTrainingText.visibility=View.GONE
                adapter = TrainingAdapter(trainings)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = adapter
            }
        }

        setupCombinedChart(ourView.findViewById(R.id.combinedChart))
    }


    fun generateKmPerDayData(trainingList: List<TrainingData>, days: Int): Map<String, Float> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today = LocalDate.now()

        // Mappa per tutti i giorni degli ultimi X giorni, inizializzati a 0
        val kmMap = mutableMapOf<String, Float>()
        for (i in days downTo 0) {
            val date = today.minusDays(i.toLong())
            kmMap[date.format(formatter)] = 0f
        }

        // Somma i km ai rispettivi giorni
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
            labels.add(date.substring(5)) // mostra solo "MM-dd"
            index++
        }

        val dataSet = LineDataSet(entries, "Km per giorno")
        dataSet.color = Color.rgb(70, 130, 180)
        dataSet.setDrawFilled(true)
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)
        dataSet.lineWidth = 2f
        dataSet.fillColor = Color.rgb(200, 220, 255)

        chart.data = LineData(dataSet)
        chart.description.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)

        // X Axis
        val xAxis = chart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.labelRotationAngle = -45f
        xAxis.granularity = 1f
        xAxis.labelCount = 5 // Mostra meno date per evitare sovrapposizione

        chart.axisLeft.axisMinimum = 0f
        chart.animateY(1000)
        chart.invalidate()
    }

    private fun setupCombinedChart(chart: CombinedChart) {
        // Line Data
        val lineEntries = listOf(
            Entry(0f, 30f),
            Entry(1f, 50f),
            Entry(2f, 45f),
            Entry(3f, 60f)
        )
        val lineDataSet = LineDataSet(lineEntries, "Trend vendite")
        lineDataSet.color = Color.MAGENTA
        val lineData = LineData(lineDataSet)

        // Bar Data
        val barEntries = listOf(
            BarEntry(0f, 10f),
            BarEntry(1f, 20f),
            BarEntry(2f, 25f),
            BarEntry(3f, 15f)
        )
        val barDataSet = BarDataSet(barEntries, "Volume")
        barDataSet.color = Color.GRAY
        val barData = BarData(barDataSet)

        // Combine
        val combinedData = CombinedData()
        combinedData.setData(lineData)
        combinedData.setData(barData)

        chart.data = combinedData
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Q1", "Q2", "Q3", "Q4"))
        chart.description.isEnabled = false
        chart.animateY(1000)
    }


}