package com.example.doggo_ourapp.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.doggo_ourapp.R
import com.example.doggo_ourapp.UserData
import com.example.doggo_ourapp.UserFirebase

import android.content.Intent

class ChatAdapter(
    val context: Context,
    val chatList: List<ChatData>)
    : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>()
{
    class ChatViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val text_name = itemView.findViewById<TextView>(R.id.txt_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]

        // Impostiamo il nome come prima
        if (UserFirebase.getCurrentUserId() == chat.user1) {
            UserFirebase.getUserByUid(chat.user2!!) { user ->
                holder.text_name.text = user?.name ?: ""
            }
        } else {
            UserFirebase.getUserByUid(chat.user1!!) { user ->
                holder.text_name.text = user?.name ?: ""
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, Message::class.java)
            intent.putExtra("chatId", chat.chatId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}
