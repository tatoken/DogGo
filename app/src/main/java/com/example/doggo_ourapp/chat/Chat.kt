package com.example.doggo_ourapp.chat

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doggo_ourapp.R
import com.example.doggo_ourapp.TrainingAdapter
import com.example.doggo_ourapp.TrainingFirebase

class Chat : Fragment(R.layout.chat_layout) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter

    private lateinit var noChatText:LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        noChatText=view.findViewById(R.id.noChatText)
        recyclerView = view.findViewById(R.id.recyclerViewTraining)

        ChatFirebase.getUserChats() { chats ->

            if (chats.isEmpty()) {
                noChatText.visibility=View.VISIBLE
            }
            else {
                noChatText.visibility=View.GONE
                adapter = ChatAdapter(requireContext(),chats)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = adapter
            }
        }
    }


}