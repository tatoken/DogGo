package com.example.doggo_ourapp.badgeTypeHandler

import com.example.doggo_ourapp.DogFirebase
import com.example.doggo_ourapp.TrainingFirebase

object TrainingDistanceHandler : BadgeHandler {
    override fun canHandle(type: String) = type.equals("trainingDistance")

    override fun getValue(onResult: (Float) -> Unit) {
        var result = 0f
        var dogsProcessed = 0

        DogFirebase.loadAllDog() { dogs ->
            if (dogs == null || dogs.isEmpty()) {
                onResult(result)
                return@loadAllDog
            }

            for (dog in dogs) {
                TrainingFirebase.loadAllTrainingsOfDog(dog.id!!) { trainings ->
                    if (trainings != null) {
                        for (training in trainings) {
                            result += training.km?.toFloatOrNull() ?: 0f
                        }
                    }
                    dogsProcessed++
                    if (dogsProcessed == dogs.size) {
                        onResult(result)
                    }
                }
            }
        }
    }

}
