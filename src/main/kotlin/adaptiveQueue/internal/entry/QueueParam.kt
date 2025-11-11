package ru.bmstu.naburnm8.adaptiveQueue.internal.entry

data class QueueParam <T>(
    val name: String,
    val weight: Double,
    val compute: (T) -> Double,
)

data class CalculatedQueueParam(
    val name: String,
    val value: Double
)