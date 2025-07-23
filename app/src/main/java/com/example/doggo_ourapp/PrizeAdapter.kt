package com.example.doggo_ourapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PrizeAdapter(
    private val prizes: List<PrizeData>,
    private val scope: CoroutineScope
) : RecyclerView.Adapter<PrizeAdapter.PrizeViewHolder>() {

    inner class PrizeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val prizeName: TextView = view.findViewById(R.id.prizeName)
        val prizeDescription: TextView = view.findViewById(R.id.prizeDescription)
        val prizeThreshold: TextView = view.findViewById(R.id.prizeThreshold)
        val prizeIcon: ImageView = view.findViewById(R.id.prizeIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrizeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_prize, parent, false)
        return PrizeViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrizeViewHolder, position: Int) {
        val prize = prizes[position]
        holder.prizeName.text = prize.name
        holder.prizeDescription.text = prize.description
        holder.prizeThreshold.text = "${prize.threshold} punti"

        scope.launch {
            val bitmap = SupabaseManager.downloadImage("prize", "${prize.name}.png")
            holder.prizeIcon.setImageBitmap(bitmap)

            holder.itemView.setOnClickListener {
                val fragment = PrizeDialogFragment(prize, bitmap!!)
                val activity = holder.itemView.context as? AppCompatActivity
                activity?.supportFragmentManager?.let {
                    fragment.show(it, "PrizeDialog")
                }
            }
        }
    }


    override fun getItemCount(): Int = prizes.size
}


