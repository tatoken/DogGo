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

class TrainingAdapter(
    private val trainings: List<TrainingData>
) : RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder>() {

    inner class TrainingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val trainingHour: TextView = view.findViewById(R.id.textHour)
        val trainingDistance: TextView = view.findViewById(R.id.kmText)
        val trainingTime: TextView = view.findViewById(R.id.timeText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_training, parent, false)
        return TrainingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val training = trainings[position]
        holder.trainingHour.text = training.hour
        holder.trainingDistance.text = training.km
        holder.trainingTime.text = training.time
    }


    override fun getItemCount(): Int = trainings.size
}


