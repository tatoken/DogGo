package com.example.doggo_ourapp

import android.content.Context
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

class BadgeAdapter(
    private val badges: List<BadgeData>,
    private val scope: CoroutineScope,
    private val compactMode: Boolean = false
) : RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    inner class BadgeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val badgeName: TextView = view.findViewById(R.id.badgeName)
        val badgeType: TextView = view.findViewById(R.id.badgeType)
        val badgeIcon: ImageView = view.findViewById(R.id.badgeIcon)
        val progressBar: com.example.doggo_ourapp.GeneralProgressBarComponent = view.findViewById(R.id.progressBarBadge)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_badge, parent, false)
        return BadgeViewHolder(view)
    }

    fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badges[position]
        holder.badgeName.text = badge.name
        holder.badgeType.text = badge.description ?: ""

        if (compactMode) {
            // Riduci dimensioni immagine
            holder.badgeIcon.layoutParams.width = 50.dpToPx(holder.itemView.context)
            holder.badgeIcon.layoutParams.height = 50.dpToPx(holder.itemView.context)
            holder.badgeIcon.requestLayout()

            // Riduci padding e margini
            holder.itemView.setPadding(8, 4, 8, 4)

            // Riduci dimensione testo
            holder.badgeName.textSize = 13f
            holder.badgeType.textSize = 11f
        }

        val threshold = badge.threshold?.toIntOrNull() ?: 0

        BadgeManager.getActualValue(badge.type!!) { progressValue ->
            val progress = progressValue.toInt()

            if (progress < threshold)
            {
                holder.progressBar.setProgressBarUpperBound(threshold)
                holder.progressBar.setLabel("You are at $progressValue out of $threshold")
                holder.progressBar.setProgressBarProgress(progress)
            }
            else
            {
                holder.progressBar.visibility = View.GONE
            }

            scope.launch {
                val bitmap = SupabaseManager.downloadImage("badge", "${badge.name}.png")

                val finalBitmap = if (progress < threshold) {
                    darkenBitmap(bitmap!!, 0.2f)
                } else {
                    bitmap
                }

                holder.badgeIcon.setImageBitmap(finalBitmap)
            }
        }
    }

    fun darkenBitmap(original: Bitmap, factor: Float): Bitmap {
        val bmp = Bitmap.createBitmap(original.width, original.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val paint = Paint()

        val colorMatrix = ColorMatrix()
        colorMatrix.setScale(factor, factor, factor, 1.0f) // R, G, B, Alpha

        val filter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = filter
        canvas.drawBitmap(original, 0f, 0f, paint)

        return bmp
    }


    override fun getItemCount(): Int = badges.size
}


