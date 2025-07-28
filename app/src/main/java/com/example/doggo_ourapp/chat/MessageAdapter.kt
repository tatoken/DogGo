package com.example.doggo_ourapp.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.doggo_ourapp.R
import com.example.doggo_ourapp.UserFirebase
import java.time.LocalDateTime

class MessageAdapter(private val messages: List<MessageData>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val content: TextView = view.findViewById(R.id.txt_content)
        val time: TextView = view.findViewById(R.id.txt_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_item, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val msg = messages[position]
        holder.content.text = msg.content

        try {
            val time = LocalDateTime.parse(msg.timestamp).toLocalTime().toString().substring(0, 5)
            holder.time.text = time
        } catch (e: Exception) {
            holder.time.text = ""
        }

        val isSentByCurrentUser = msg.sender == UserFirebase.getCurrentUserId()

        val layoutParams = holder.content.layoutParams as ConstraintLayout.LayoutParams
        val timeLayoutParams = holder.time.layoutParams as ConstraintLayout.LayoutParams

        if (isSentByCurrentUser) {
            layoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID

            timeLayoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET
            timeLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        } else {
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET
            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID

            timeLayoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET
            timeLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        }

        holder.content.layoutParams = layoutParams
        holder.time.layoutParams = timeLayoutParams
    }


    override fun getItemCount() = messages.size
}
