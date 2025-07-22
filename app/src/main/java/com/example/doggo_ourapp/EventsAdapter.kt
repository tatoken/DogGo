package com.example.doggo_ourapp

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.doggo_ourapp.badgeTypeHandler.BadgeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class EventsAdapter(
    private val events: List<EventData>,
    private val scope: CoroutineScope
) : RecyclerView.Adapter<EventsAdapter.BadgeViewHolder>() {

    inner class BadgeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textHour: TextView = view.findViewById(R.id.textHour)
        val titleText: TextView = view.findViewById(R.id.titleText)
        val descriptionText: TextView = view.findViewById(R.id.descriptionText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_layout, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val event = events[position]
        holder.textHour.text = event.time
        holder.titleText.text = event.title
        holder.descriptionText.text = event.description
    }

    override fun getItemCount(): Int = events.size
}


