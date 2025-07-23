package com.example.doggo_ourapp

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object TrainingFirebase {

    fun saveTraining(training: TrainingData, onResult: (Boolean) -> Unit) {
        DogFirebase.getActualDog(){ actualDogCorrect ->
            if(actualDogCorrect==null)
            {
                onResult(false)
            }
            else {
                val actualTrainingRef = FirebaseDB.getMDbRef()
                    .child("user")
                    .child(UserFirebase.getCurrentUserId())
                    .child("dog")
                    .child(actualDogCorrect)
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
    }

    fun loadAllTrainingsOfDog(dogId:String,onResult: (List<TrainingData>?) -> Unit) {
        val trainingList = mutableListOf<TrainingData>()
        val trainingRef = FirebaseDB.getMDbRef()
            .child("user")
            .child(UserFirebase.getCurrentUserId())
            .child("dog")
            .child(dogId)
            .child("training")

        trainingRef.get().addOnSuccessListener { dataSnapshot ->
            for (child in dataSnapshot.children) {
                val training = child.getValue(TrainingData::class.java)
                training?.let {
                    it.id = child.key
                    trainingList.add(it)
                }
            }
            onResult(trainingList)
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
            onResult(null)
        }
    }

    fun loadAllTrainingsOfActualDog(onResult: (List<TrainingData>?) -> Unit) {
        DogFirebase.getActualDog(){ actualDogCorrect ->
            if (actualDogCorrect == null) {
                onResult(null)
            } else {
                val trainingRef = FirebaseDB.getMDbRef()
                    .child("user")
                    .child(UserFirebase.getCurrentUserId())
                    .child("dog")
                    .child(actualDogCorrect)
                    .child("training")

                trainingRef.get().addOnSuccessListener { dataSnapshot ->
                    val trainingList = mutableListOf<TrainingData>()
                    for (child in dataSnapshot.children) {
                        val training = child.getValue(TrainingData::class.java)
                        training?.let {
                            it.id = child.key
                            trainingList.add(it)
                        }
                    }
                    onResult(trainingList)
                }.addOnFailureListener { exception ->
                    exception.printStackTrace()
                    onResult(null)
                }
            }
        }
    }

    fun loadTraining(trainingId: String, onResult: (TrainingData?) -> Unit) {

        DogFirebase.getActualDog(){ actualDog ->

            if (actualDog == null) {
                onResult(null)
            } else {
                val trainingRef = FirebaseDB.getMDbRef().child("user").child(UserFirebase.getCurrentUserId()).child("dog").child(actualDog).child("training").child(trainingId)
                trainingRef.get().addOnSuccessListener { snapshot ->
                    val training = snapshot.getValue(TrainingData::class.java)
                    onResult(training)
                }.addOnFailureListener {
                    it.printStackTrace()
                    onResult(null)
                }
            }
        }
    }

    fun loadTrainingsOfLastDays(days: Int, onResult: (List<TrainingData>) -> Unit) {
        DogFirebase.getActualDog { actualDog ->
            if (actualDog == null) {
                onResult(emptyList())
                return@getActualDog
            }

            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val today = LocalDate.now()
            val trainingList = mutableListOf<TrainingData>()

            val trainingRef = FirebaseDB.getMDbRef()
                .child("user")
                .child(UserFirebase.getCurrentUserId())
                .child("dog")
                .child(actualDog)
                .child("training")

            trainingRef.get().addOnSuccessListener { snapshot ->
                for (child in snapshot.children) {
                    val training = child.getValue(TrainingData::class.java)
                    training?.let {
                        it.id = child.key

                        val dateString = it.date
                        if (!dateString.isNullOrBlank()) {
                            try {
                                val trainingDate = LocalDate.parse(dateString, formatter)
                                val daysBetween = ChronoUnit.DAYS.between(trainingDate, today)
                                if (daysBetween in 0..days) {
                                    trainingList.add(it)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                trainingList.reverse()
                onResult(trainingList)
            }.addOnFailureListener {
                it.printStackTrace()
                onResult(emptyList())
            }
        }
    }

}


