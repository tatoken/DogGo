package com.example.doggo_ourapp.badgeTypeHandler

object BadgeManager {
    private val handlers = listOf(
        TrainingDistanceHandler
    )

    fun getActualValue(type: String, onResult: (Float) -> Unit) {
        handlers.find { it.canHandle(type) }
            ?.getValue(onResult)
            ?: onResult(0f)
    }
}
