package com.example.doggo_ourapp

import java.io.Serializable

data class EventData(
    var id: String?,
    val title: String,
    val description: String,
    val time: String,
    val date: String,
): Serializable
