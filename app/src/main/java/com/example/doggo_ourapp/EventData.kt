package com.example.doggo_ourapp

import java.io.Serializable

data class EventData(
    var id: String? =null,
    val title: String?=null,
    val description: String?=null,
    val time: String?=null,
    val date: String?=null,
): Serializable
