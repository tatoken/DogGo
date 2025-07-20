package com.example.doggo_ourapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Prizes : Fragment(R.layout.prizes_layout) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PrizeAdapter
    private lateinit var dataText: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataText = view.findViewById(R.id.dataText)
        recyclerView = view.findViewById(R.id.recyclerViewPrize)

        dataText.text = "Premi complessivi"

        getPrizePerData { prizes ->
            adapter = PrizeAdapter(prizes, viewLifecycleOwner.lifecycleScope)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter
        }
    }

    private fun getPrizePerData(callback: (List<PrizeData>) -> Unit) {
        PrizeFirebase.loadAllPrizes { result ->
            if (result.isNullOrEmpty()) {
                println("⚠️ Nessun badge caricato!")
            } else {
                println("✅ Badge caricati: ${result.size}")
                callback(result)
            }
        }
    }

}
