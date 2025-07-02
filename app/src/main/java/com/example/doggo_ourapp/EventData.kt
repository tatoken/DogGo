package com.example.doggo_ourapp

import java.time.LocalDate

data class EventData(
    var id: String?,
    var name: String?,
    var date: LocalDate?,
    var type: String?,
    var description: String?
)