package com.example.doggo_ourapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.doggo_ourapp.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class Gym: Fragment(R.layout.gym_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBarChart(view.findViewById(R.id.barChart))
        setupPieChart(view.findViewById(R.id.pieChart))
        setupCombinedChart(view.findViewById(R.id.combinedChart))
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

    private fun setupPieChart(chart: PieChart) {
        val entries = listOf(
            PieEntry(40f, "Cibo"),
            PieEntry(30f, "Affitto"),
            PieEntry(20f, "Trasporti"),
            PieEntry(10f, "Altro")
        )
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)

        val data = PieData(dataSet)
        chart.data = data
        chart.setUsePercentValues(true)
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

