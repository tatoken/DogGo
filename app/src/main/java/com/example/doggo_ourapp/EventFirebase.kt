package com.example.doggo_ourapp

object EventFirebase{

    fun saveEvent(event: EventData, onResult: (Boolean) -> Unit) {
        DogFirebase.getActualDog(){ actualDog ->
            if(actualDog==null)
            {
                onResult(false)
            }
            else {
                val actualEventRef = FirebaseDB.getMDbRef()
                    .child("user")
                    .child(UserFirebase.getCurrentUserId())
                    .child("dog")
                    .child(actualDog)
                    .child("event")

                val actualEventRefId = actualEventRef.push()
                event.id = actualEventRefId.key
                actualEventRefId.setValue(event).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                }

            }
        }
    }

    fun loadAllEvents(onResult: (List<EventData>?) -> Unit) {
        DogFirebase.getActualDog(){ actualDog ->

            if (actualDog == null) {
                onResult(null)
            } else {
                val eventRef = FirebaseDB.getMDbRef()
                    .child("user")
                    .child(UserFirebase.getCurrentUserId())
                    .child("dog")
                    .child(actualDog)
                    .child("event")

                eventRef.get().addOnSuccessListener { dataSnapshot ->
                    val eventList = mutableListOf<EventData>()
                    for (child in dataSnapshot.children) {
                        val event = child.getValue(EventData::class.java)
                        event?.let {
                            it.id = child.key
                            eventList.add(it)
                        }
                    }
                    onResult(eventList)
                }.addOnFailureListener { exception ->
                    exception.printStackTrace()
                    onResult(null)
                }
            }
        }
    }

    fun loadEvent(eventId: String, onResult: (EventData?) -> Unit) {

        DogFirebase.getActualDog(){ actualDog ->

            if (actualDog == null) {
                onResult(null)
            } else {
                val eventRef = FirebaseDB.getMDbRef().child("user").child(UserFirebase.getCurrentUserId()).child("dog").child(actualDog).child("event").child(eventId)
                eventRef.get().addOnSuccessListener { snapshot ->
                    val event = snapshot.getValue(EventData::class.java)
                    onResult(event)
                }.addOnFailureListener {
                    it.printStackTrace()
                    onResult(null)
                }
            }
        }
    }


    fun loadEventsByDate(targetDate: String, onResult: (List<EventData>) -> Unit) {
        loadAllEvents { allEvents ->
            val filteredEvents = allEvents?.filter { it.date == targetDate } ?: emptyList()
            onResult(filteredEvents)
        }
    }


}


