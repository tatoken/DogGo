package com.example.doggo_ourapp

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

object EventFirebase {

    fun saveEvent(event: EventData, onResult: (Boolean) -> Unit) {
        //val actualDog = DogFirebase.getActualDog()
        val actualDog = "-OVSRjvLU7TVDVNcU53J"

        if (actualDog == null) {
            onResult(false)
        } else {
            val eventRef = FirebaseDB.getMDbRef()
                .child("user")
                .child(UserFirebase.getCurrentUserId())
                .child("dog")
                .child(actualDog)
                .child("event")

            val eventRefId = eventRef.push()
            event.id = eventRefId.key
            eventRefId.setValue(event).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
        }
    }

    fun getEvents(onResult: (List<EventData>) -> Unit) {
        //val actualDog = DogFirebase.getActualDog()
        val actualDog = "-OVSRjvLU7TVDVNcU53J"

        if (actualDog == null) {
            onResult(emptyList())
        } else {
            val eventRef = FirebaseDB.getMDbRef()
                .child("user")
                .child(UserFirebase.getCurrentUserId())
                .child("dog")
                .child(actualDog)
                .child("event")

            eventRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val eventList = mutableListOf<EventData>()
                    for (child in snapshot.children) {
                        val event = child.getValue(EventData::class.java)
                        event?.let { eventList.add(it) }
                    }
                    onResult(eventList)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
        }
    }

    fun getEvent(id: String, onResult: (EventData?) -> Unit) {
        //val actualDog = DogFirebase.getActualDog()
        val actualDog = "-OVSRjvLU7TVDVNcU53J"

        if (actualDog == null) {
            onResult(null)
        } else {
            val eventRef = FirebaseDB.getMDbRef()
                .child("user")
                .child(UserFirebase.getCurrentUserId())
                .child("dog")
                .child(actualDog)
                .child("event")
                .child(id)

            eventRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val event = snapshot.getValue(EventData::class.java)
                    onResult(event)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(null)
                }
            })
        }
    }

}