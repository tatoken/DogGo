package com.example.doggo_ourapp

import java.time.LocalDate

data class EventData(
    var id: String?=null,
    var name: String?=null,
    var date: String?=null,
    var hour: String?=null,
    var type: String?=null,
    var description: String?=null
)