package ru.bmstu.naburnm8.adaptiveQueue.internal.rule

import java.util.UUID


class PriorityRule<T> (
    val identifier: UUID = UUID.randomUUID(),
    val condition: (T) -> Boolean,
    val calculate: (T) -> Double
) {
}