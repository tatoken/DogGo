package com.example.doggo_ourapp

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

object TrainingFirebase {

    fun saveTraining(training: TrainingData, onResult: (Boolean) -> Unit) {
        //val actualDog = DogFirebase.getActualDog()
        val actualDog = "-OVSRjvLU7TVDVNcU53J"

        if(actualDog==null)
        {
            onResult(false)
        }
        else {
            val actualTrainingRef = FirebaseDB.getMDbRef()
                .child("user")
                .child(UserFirebase.getCurrentUserId())
                .child("dog")
                .child(actualDog)
                .child("training")

            val actualTrainingRefId = actualTrainingRef.push()
            training.id = actualTrainingRefId.key
            actualTrainingRefId.setValue(training).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true)
                } else {
                    onResult(false)
                }
            }

        }
    }

    fun getTrainings(onResult: (List<TrainingData>) -> Unit) {
        //val actualDog = DogFirebase.getActualDog()
        val actualDog = "-OVSRjvLU7TVDVNcU53J"

        if (actualDog == null) {
            onResult(emptyList())
        } else {
            val trainingRef = FirebaseDB.getMDbRef()
                .child("user")
                .child(UserFirebase.getCurrentUserId())
                .child("dog")
                .child(actualDog)
                .child("training")

            trainingRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val trainingList = mutableListOf<TrainingData>()
                    for (child in snapshot.children) {
                        val training = child.getValue(TrainingData::class.java)
                        training?.let { trainingList.add(it) }
                    }
                    onResult(trainingList)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList()) // oppure gestisci errore
                }
            })
        }
    }


    fun getTraining(id: String, onResult: (TrainingData?) -> Unit) {
        //val actualDog = DogFirebase.getActualDog()
        val actualDog = "-OVSRjvLU7TVDVNcU53J"

        if (actualDog == null) {
            onResult(null)
        } else {
            val trainingRef = FirebaseDB.getMDbRef()
                .child("user")
                .child(UserFirebase.getCurrentUserId())
                .child("dog")
                .child(actualDog)
                .child("training")
                .child(id)

            trainingRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val training = snapshot.getValue(TrainingData::class.java)
                    onResult(training)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(null) // oppure gestisci errore
                }
            })
        }
    }

    fun updateTraining(training: TrainingData, onResult: (Boolean) -> Unit) {
        //val actualDog = DogFirebase.getActualDog()
        val actualDog = "-OVSRjvLU7TVDVNcU53J"

        if (actualDog == null || training.id == null) {
            onResult(false)
            return
        }

        val trainingRef = FirebaseDB.getMDbRef()
            .child("user")
            .child(UserFirebase.getCurrentUserId())
            .child("dog")
            .child(actualDog)
            .child("training")
            .child(training.id!!)

        trainingRef.setValue(training).addOnCompleteListener { task ->
            onResult(task.isSuccessful)
        }
    }


}
