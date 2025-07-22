package com.example.doggo_ourapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Prizes : Fragment(R.layout.prizes_layout) {

    private lateinit var pointText:TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PrizeAdapter

    private lateinit var recyclerViewAcquired: RecyclerView
    private lateinit var adapterAcquired: PrizeAcquireAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener("refresh_request", viewLifecycleOwner) { _, _ ->
            reloadData(view)
        }

        pointText=view.findViewById(R.id.pointText)
        setPointsValue()

        recyclerView = view.findViewById(R.id.recyclerViewPrize)
        getPrizePerData { prizes ->
            adapter = PrizeAdapter(prizes, viewLifecycleOwner.lifecycleScope)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter
        }

        recyclerViewAcquired = view.findViewById(R.id.recyclerViewPrizeAcquired)
        getPrizePerData { prizes ->
            getPrizeAcquiredPerData(){
                prizesAcquired->
                val quantity = mutableListOf<String>()
                val prizesAllInfoAcquired = mutableListOf<PrizeData>()

                for (prizeAcquired in prizesAcquired) {
                    quantity.add(prizeAcquired.quantity.toString())

                    val matchingPrize = prizes.find { it.id == prizeAcquired.idPrize }
                    if (matchingPrize != null) {
                        prizesAllInfoAcquired.add(matchingPrize)
                    }
                }

                adapterAcquired = PrizeAcquireAdapter(prizesAllInfoAcquired,quantity, viewLifecycleOwner.lifecycleScope)
                recyclerViewAcquired.layoutManager = LinearLayoutManager(requireContext())
                recyclerViewAcquired.adapter = adapterAcquired
            }
        }
    }

    private fun reloadData(view:View) {
        setPointsValue()
        recyclerViewAcquired = view.findViewById(R.id.recyclerViewPrizeAcquired)
        getPrizePerData { prizes ->
            getPrizeAcquiredPerData(){
                    prizesAcquired->
                val quantity = mutableListOf<String>()
                val prizesAllInfoAcquired = mutableListOf<PrizeData>()

                for (prizeAcquired in prizesAcquired) {
                    quantity.add(prizeAcquired.quantity.toString())

                    val matchingPrize = prizes.find { it.id == prizeAcquired.idPrize }
                    if (matchingPrize != null) {
                        prizesAllInfoAcquired.add(matchingPrize)
                    }
                }

                adapterAcquired = PrizeAcquireAdapter(prizesAllInfoAcquired,quantity, viewLifecycleOwner.lifecycleScope)
                recyclerViewAcquired.layoutManager = LinearLayoutManager(requireContext())
                recyclerViewAcquired.adapter = adapterAcquired
            }
        }
    }


    private fun setPointsValue() {
        UserFirebase.getCurrentUserPoints() {points->
            if(points!=null)
            {
                pointText.text="Punti rimanenti: "+points
            }
            else
            {

                pointText.text="Punti rimanenti: 0"
            }
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

    private fun getPrizeAcquiredPerData(callback: (List<PrizeAchievedData>) -> Unit) {
        PrizeFirebase.getUserPrizes { result ->
            if (result.isNullOrEmpty()) {
                println("⚠️ Nessun badge caricato!")
            } else {
                println("✅ Badge caricati: ${result.size}")
                callback(result)
            }
        }
    }

}
