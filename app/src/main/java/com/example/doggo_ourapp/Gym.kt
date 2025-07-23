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
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.PieChart
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

        setupBarChart(view.findViewById(R.id.barChart))
        setupCombinedChart(view.findViewById(R.id.combinedChart))

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

    private fun setupBarChart(chart: BarChart) {
        val entries = listOf(
            BarEntry(0f, 120f),
            BarEntry(1f, 80f),
            BarEntry(2f, 140f),
            BarEntry(3f, 100f)
        )
        val dataSet = BarDataSet(entries, "Utenti per giorno")
        dataSet.color = Color.rgb(60, 130, 200)

        chart.data = BarData(dataSet)
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Lun", "Mar", "Mer", "Gio"))
        chart.description.isEnabled = false
        chart.animateY(1000)
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