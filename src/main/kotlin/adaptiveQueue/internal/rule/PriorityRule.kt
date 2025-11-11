package ru.bmstu.naburnm8.adaptiveQueue.internal.rule

import java.util.UUID


open class PriorityRule<T> (
    val identifier: UUID = UUID.randomUUID(),
    protected val condition: (T) -> Boolean,
    protected val calculate: (T) -> Double
) {
    fun condition(model: T): Boolean {
        return condition.invoke(model)
    }
    fun calculate(model: T): Double {
        return calculate.invoke(model)
    }
}