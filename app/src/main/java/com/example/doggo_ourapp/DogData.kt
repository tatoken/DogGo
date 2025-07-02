package com.example.doggo_ourapp

data class DogData(
    var id: String? = null,
    var name: String? = null,
    var breed: String? = null,
    var sex: String? = null,
    var age: String? = null,
    var microchip: String? = null,
    var weight: String? = null,
    var vetTelephone: String? = null,
    var events: MutableList<EventData>? = mutableListOf(),
    var trainingAchieved: MutableList<TrainingData>? = mutableListOf(),
    var diet: DietData? = null
)