package ru.bmstu.naburnm8.adaptiveQueue.inner.manager

class PriorityRule<T> (
    val condition: (T) -> Boolean,
    val calculate: (T) -> Double
) {
}