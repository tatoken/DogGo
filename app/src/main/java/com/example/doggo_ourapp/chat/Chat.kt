package com.example.doggo_ourapp.chat

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doggo_ourapp.R
import com.example.doggo_ourapp.TrainingAdapter
import com.example.doggo_ourapp.TrainingFirebase
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Chat : Fragment(R.layout.chat_layout) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter

    private lateinit var fab: FloatingActionButton

    private lateinit var noChatText:LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        fab= view.findViewById(R.id.btn_start)

        noChatText=view.findViewById(R.id.noChatText)
        recyclerView = view.findViewById(R.id.recyclerViewChat)

        loadChat()

        fab.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext()) // Definito PRIMA
            val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
            bottomSheetDialog.setContentView(bottomSheetView)

            val editFriendCode = bottomSheetView.findViewById<EditText>(R.id.editFriendCode)
            val btnSend = bottomSheetView.findViewById<Button>(R.id.btnSendCode)

            btnSend.setOnClickListener {
                val code = editFriendCode.text.toString()
                if (code.isNotBlank()) {
                    Log.d("ChatFirebase", "Not blanck .")
                    ChatFirebase.createChatWithUser(code) { result ->
                        if (result == null) {
                            Log.d("ChatFirebase", "null .")
                            Toast.makeText(requireContext(), "Utente non presente", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d("ChatFirebase", "Not null .")
                            Toast.makeText(requireContext(), "Chat creata con successo", Toast.LENGTH_SHORT).show()
                            bottomSheetDialog.dismiss()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Inserisci un codice valido", Toast.LENGTH_SHORT).show()
                }
            }

            bottomSheetDialog.setOnDismissListener {
                loadChat()
            }

            bottomSheetDialog.show()
        }

    }

    private fun loadChat() {
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