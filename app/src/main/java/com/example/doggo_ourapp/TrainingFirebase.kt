package com.example.doggo_ourapp

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
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
            onResult(null)
        }

        onResult(trainingList)
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

        DogFirebase.getActualDog(){ actualDogCorrect ->
            val actualDog = "-OU9JYKmtiQQUxkHcxoW"

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
}


