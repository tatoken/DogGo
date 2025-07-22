package com.example.doggo_ourapp

data class TrainingData(
    var id: String?,
    var date: String?,
    var time: String?,
    var km: String?
){
    constructor() : this(null, null, null, null)
}