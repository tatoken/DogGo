package com.example.doggo_ourapp.badgeTypeHandler

interface BadgeHandler {
    fun canHandle(type: String): Boolean
    fun getValue(onResult: (Float) -> Unit)
}
