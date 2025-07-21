package com.example.doggo_ourapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Scoreboard : Fragment(R.layout.scoreboard_layout) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScoreAdapter
    private lateinit var dataText: TextView

    private lateinit var pointText:TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataText = view.findViewById(R.id.dataText)
        recyclerView = view.findViewById(R.id.recyclerViewScores)

        pointText=view.findViewById(R.id.pointText)

        getTopFiftyUser { users ->
            adapter = ScoreAdapter(users, viewLifecycleOwner.lifecycleScope)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter
        }

        UserFirebase.getCurrentUserTotalPoints() { result->
            pointText.text=result
        }

    }

    private fun getTopFiftyUser(callback: (List<UserData>) -> Unit) {
        UserFirebase.loadTopUsers(50) { result ->
            if (result.isNullOrEmpty()) {
                println("⚠️ Nessun user caricato!")
            } else {
                println("✅ user caricati: ${result.size}")
                callback(result)
            }
        }
    }


}
