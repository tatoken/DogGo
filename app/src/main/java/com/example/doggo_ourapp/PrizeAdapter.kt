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

class PrizeAdapter(
    private val prizes: List<PrizeData>,
    private val scope: CoroutineScope
) : RecyclerView.Adapter<PrizeAdapter.BadgeViewHolder>() {

    inner class BadgeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val prizeName: TextView = view.findViewById(R.id.prizeName)
        val prizeDescription: TextView = view.findViewById(R.id.prizeDescription)
        val prizeThreshold: TextView = view.findViewById(R.id.prizeThreshold)
        val prizeIcon: ImageView = view.findViewById(R.id.prizeIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_prize, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val prize = prizes[position]
        holder.prizeName.text = prize.name
        holder.prizeDescription.text = prize.description
        holder.prizeThreshold.text = prize.threshold +" punti"

        scope.launch {
            val bitmap = SupabaseManager.downloadImage("prize", "${prize.name}.png")
            holder.prizeIcon.setImageBitmap(bitmap)
        }
    }

    override fun getItemCount(): Int = prizes.size
}


