package com.example.doggo_ourapp.chat

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doggo_ourapp.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Message : AppCompatActivity() {

    private lateinit var recyclerMessages: RecyclerView
    private lateinit var adapter: MessageAdapter


    private lateinit var editMessage: EditText
    private lateinit var btnSend: ImageButton
    private var chatId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.message_layout)

        chatId = intent.getStringExtra("chatId")

        editMessage = findViewById(R.id.edit_message)
        btnSend = findViewById(R.id.btn_send)

        recyclerMessages = findViewById(R.id.recycler_messages)


        btnSend.setOnClickListener {
            val content = editMessage.text.toString().trim()

            if (content.isEmpty()) {
                Toast.makeText(this, "Il messaggio è vuoto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (chatId != null) {
                ChatFirebase.sendMessage(chatId!!, content) { success ->
                    if (success) {
                        editMessage.text.clear()
                        getMessages()
                    } else {
                        Toast.makeText(this, "Errore durante l'invio", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Chat ID mancante", Toast.LENGTH_SHORT).show()
            }
        }

        if (chatId != null) {
            getMessages()
        }
    }

    private fun getMessages() {
        ChatFirebase.getChatMessages(chatId!!) { messages ->
            if (messages.isEmpty()) {
                println("Nessun messaggio")
            } else {
                adapter = MessageAdapter(messages)
                recyclerMessages.layoutManager = LinearLayoutManager(this)
                recyclerMessages.adapter = adapter
            }
        }
    }

}
