package com.example.doggo_ourapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Badge : Fragment(R.layout.badge_layout) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BadgeAdapter
    private lateinit var dataText: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataText = view.findViewById(R.id.dataText)
        recyclerView = view.findViewById(R.id.recyclerViewBadge)

        dataText.text = "Medaglie complessive"

        getBadgePerData { badges ->
            adapter = BadgeAdapter(badges, viewLifecycleOwner.lifecycleScope)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter
        }
    }

    private fun getBadgePerData(callback: (List<BadgeData>) -> Unit) {
        BadgeFirebase.loadAllBadges { result ->
            if (result.isNullOrEmpty()) {
                println("⚠️ Nessun badge caricato!")
            } else {
                println("✅ Badge caricati: ${result.size}")
                callback(result)
            }
        }
    }



}
