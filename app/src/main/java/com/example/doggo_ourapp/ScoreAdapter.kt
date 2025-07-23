package com.example.doggo_ourapp

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.doggo_ourapp.badgeTypeHandler.BadgeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ScoreAdapter(
    private val users: List<UserData>
) : RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

    inner class ScoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val conponent:LinearLayout=view.findViewById(R.id.component)
        val name: TextView = view.findViewById(R.id.name)
        val points: TextView = view.findViewById(R.id.points)
        val positionImage: ImageView = view.findViewById(R.id.positionImage)
        val position:TextView=view.findViewById(R.id.position)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trophy_page_leaderboard_component, parent, false)
        return ScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val user = users[position]

        if(position<3)
        {
            val context = holder.itemView.context
            val resourceName = "medal_${position + 1}"
            val resId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)

            if (resId != 0) {
                holder.positionImage.setImageResource(resId)
            } else {
                Log.w("DrawableWarning", "Drawable not found for: $resourceName")
            }
        }
        else
        {
            holder.positionImage.visibility = View.GONE
            println("Siamo oltre il 3 ")

            holder.position.text = (position + 1).toString()

            val layoutParams = holder.position.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 3f
            holder.position.layoutParams = layoutParams

        }

        if(user.uid.equals(UserFirebase.getCurrentUserId()))
        {
            holder.conponent.setBackgroundResource(R.drawable.score_selected)
        }

        holder.name.text = user.name
        holder.points.text = user.totalPoints


    }

    override fun getItemCount(): Int = users.size
}


