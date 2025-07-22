package com.example.doggo_ourapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Locale

class Gym : Fragment() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_training, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadChartsFromDatabase()

        view.findViewById<Button>(R.id.btn_start).setOnClickListener {
            if (areLocationPermissionsGranted()) {
                try {
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            startActivity(Intent(requireContext(), TrackingActivity::class.java))
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

    override fun onResume() {
        super.onResume()
        loadChartsFromDatabase() // Ricarica i dati e aggiorna i grafici ogni volta che il fragment torna in primo piano
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

    private fun loadChartsFromDatabase() {
        TrainingFirebase.getTrainings { trainingList ->
            if (trainingList.isEmpty()) return@getTrainings

            val barChart = view?.findViewById<com.github.mikephil.charting.charts.BarChart>(R.id.bar_chart)
            val lineChart = view?.findViewById<com.github.mikephil.charting.charts.LineChart>(R.id.line_chart)

            val sortedList = trainingList.sortedBy { it.date }
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dayFormat = SimpleDateFormat("EEE", Locale.getDefault()) // es. Lun, Mar, ecc.

            // --- BarChart: somma km per giorno della settimana ---
            val dayToKmMap = linkedMapOf<String, Float>(
                "Lun" to 0f, "Mar" to 0f, "Mer" to 0f, "Gio" to 0f,
                "Ven" to 0f, "Sab" to 0f, "Dom" to 0f
            )

            for (training in sortedList) {
                val dateStr = training.date ?: continue
                val km = training.km?.toFloatOrNull() ?: continue

                try {
                    val date = formatter.parse(dateStr)
                    val day = dayFormat.format(date!!).replaceFirstChar { it.uppercaseChar() }
                    if (dayToKmMap.containsKey(day)) {
                        dayToKmMap[day] = dayToKmMap.getOrDefault(day, 0f) + km
                    }
                } catch (_: Exception) {}
            }

            val barEntries = dayToKmMap.entries.mapIndexed { index, entry ->
                BarEntry(index.toFloat(), entry.value)
            }

            barChart?.apply {
                val barDataSet = BarDataSet(barEntries, "Km per giorno")
                barDataSet.color = ContextCompat.getColor(requireContext(), R.color.orange_500)
                data = BarData(barDataSet)
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.myWhite))
                xAxis.valueFormatter = IndexAxisValueFormatter(dayToKmMap.keys.toList())
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                axisLeft.axisMinimum = 0f
                axisRight.isEnabled = false
                xAxis.setDrawGridLines(false)
                axisLeft.setDrawGridLines(false)
                animateY(2000, Easing.EaseInOutCubic)
                description.isEnabled = false
                invalidate()
            }

            // --- LineChart: km e tempo nel tempo ---
            val kmEntries = mutableListOf<Entry>()
            val timeEntries = mutableListOf<Entry>()

            sortedList.forEachIndexed { index, training ->
                val km = training.km?.toFloatOrNull() ?: return@forEachIndexed
                val timeStr = training.time ?: return@forEachIndexed
                val parts = timeStr.split(":")
                val minutes = parts.getOrNull(0)?.toFloatOrNull() ?: 0f
                val seconds = parts.getOrNull(1)?.toFloatOrNull() ?: 0f
                val totalTime = minutes + (seconds / 60f)

                kmEntries.add(Entry(index.toFloat(), km))
                timeEntries.add(Entry(index.toFloat(), totalTime))
            }

            lineChart?.apply {
                val kmDataSet = LineDataSet(kmEntries, "Km").apply {
                    color = ContextCompat.getColor(requireContext(), R.color.orange_500)
                    setCircleColor(color)
                    setDrawFilled(true)
                    fillColor = ContextCompat.getColor(requireContext(), R.color.orange_700)
                }

                val timeDataSet = LineDataSet(timeEntries, "Tempo (min)").apply {
                    color = ContextCompat.getColor(requireContext(), R.color.teal_700)
                    setCircleColor(color)
                    setDrawFilled(true)
                    fillColor = ContextCompat.getColor(requireContext(), R.color.teal_200)
                }

                data = LineData(kmDataSet, timeDataSet)
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.myWhite))
                xAxis.valueFormatter = IndexAxisValueFormatter(sortedList.map { it.date?.substring(5) ?: "" })
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                axisLeft.axisMinimum = 0f
                axisRight.isEnabled = false
                animateX(2000, Easing.EaseInOutCubic)
                description.isEnabled = false
                invalidate()
            }
        }
    }


}
