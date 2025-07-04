package com.example.doggo_ourapp

object TrainingFirebase {

    fun saveTraining(training: TrainingData, onResult: (Boolean) -> Unit) {
        DogFirebase.getActualDog(){ actualDogCorrect ->
            val actualDog="-OU9JYKmtiQQUxkHcxoW"
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
    }

    fun loadAllTrainings(onResult: (List<TrainingData>?) -> Unit) {
        DogFirebase.getActualDog(){ actualDogCorrect ->
            val actualDog="-OU9JYKmtiQQUxkHcxoW"

            if (actualDog == null) {
                onResult(null)
            } else {
                val trainingRef = FirebaseDB.getMDbRef()
                    .child("user")
                    .child(UserFirebase.getCurrentUserId())
                    .child("dog")
                    .child(actualDog)
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


