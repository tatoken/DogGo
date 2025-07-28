package com.example.doggo_ourapp.chat

import com.example.doggo_ourapp.FirebaseDB
import com.example.doggo_ourapp.UserFirebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ChatFirebase {

    private fun getChatRef() = FirebaseDB.getMDbRef().child("chat")
    private fun getCurrentUserId() = UserFirebase.getCurrentUserId()

    fun createChatWithUser(otherUserUid: String, onComplete: (String?) -> Unit) {
        val currentUserUid = getCurrentUserId()
        val chatRef = getChatRef()

        UserFirebase.getUserByUid(otherUserUid){
            result->
            if(result!=null)
            {
                chatRef.get().addOnSuccessListener { snapshot ->
                    var existingChatId: String? = null

                    for (child in snapshot.children) {
                        val user1 = child.child("user1").value as? String
                        val user2 = child.child("user2").value as? String

                        if (
                            (user1 == currentUserUid && user2 == otherUserUid) ||
                            (user1 == otherUserUid && user2 == currentUserUid)
                        ) {
                            existingChatId = child.key
                            break
                        }
                    }

                    if (existingChatId != null) {
                        onComplete(existingChatId)
                    } else {
                        // Non esiste una chat, la creiamo
                        val newChatRef = chatRef.push()
                        val chatData = mapOf(
                            "user1" to currentUserUid,
                            "user2" to otherUserUid
                        )

                        newChatRef.setValue(chatData).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onComplete(newChatRef.key)
                            } else {
                                onComplete(null)
                            }
                        }
                    }
                }.addOnFailureListener {
                    onComplete(null)
                }
            }
            else
            {
                onComplete(null)
            }
        }


    }



    fun sendMessage(chatId: String, content: String, onComplete: (Boolean) -> Unit) {
        val messageRef = getChatRef().child(chatId).child("messages").push()
        val messageData = mapOf(
            "sender" to getCurrentUserId(),
            "content" to content,
            "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        )

        messageRef.setValue(messageData).addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }

    fun getUserChats(onResult: (List<ChatData>) -> Unit) {
        val currentUserId = getCurrentUserId()
        getChatRef().get().addOnSuccessListener { snapshot ->
            val chats = mutableListOf<ChatData>()
            for (child in snapshot.children) {
                val user1 = child.child("user1").getValue(String::class.java)
                val user2 = child.child("user2").getValue(String::class.java)
                if (user1 == currentUserId || user2 == currentUserId) {
                    chats.add(ChatData(child.key, user1, user2))
                }
            }
            onResult(chats)
        }.addOnFailureListener {
            it.printStackTrace()
            onResult(emptyList())
        }
    }

    fun getChatMessages(chatId: String, onResult: (List<MessageData>) -> Unit) {
        getChatRef().child(chatId).child("messages").get().addOnSuccessListener { snapshot ->
            val messages = snapshot.children.mapNotNull {
                val sender = it.child("sender").getValue(String::class.java)
                val content = it.child("content").getValue(String::class.java)
                val timestamp = it.child("timestamp").getValue(String::class.java)
                if (sender != null && content != null && timestamp != null) {
                    MessageData(sender, content, timestamp)
                } else null
            }.sortedBy { it.timestamp }
            onResult(messages)
        }.addOnFailureListener {
            it.printStackTrace()
            onResult(emptyList())
        }
    }
}
