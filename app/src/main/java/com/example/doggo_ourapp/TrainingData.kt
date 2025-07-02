package com.example.doggo_ourapp

import java.time.LocalDate

data class TrainingData(
    var id: String?,
    var date: LocalDate?,
    var status: String?,
    var time: String?,
    var km: String?
)