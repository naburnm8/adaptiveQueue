package ru.bmstu.naburnm8.adaptiveQueue.internal

import ru.bmstu.naburnm8.adaptiveQueue.internal.entry.QueueEntry
import ru.bmstu.naburnm8.adaptiveQueue.internal.rule.RuleManager

class PriorityEngine<T> (
    private val ruleManager: RuleManager<T>,
) {
    fun calculate(entry: QueueEntry<T>): Double {
        val calculated = entry.calculateActiveParams()
        val calculatedRules = ruleManager.calculateRules(entry)

        var accumulator = 0.0

        for (calculatedVal in calculated) {
            accumulator += calculatedVal.value
        }

        for (calculatedRule in calculatedRules) {
            accumulator += calculatedRule
        }

        accumulator /= calculated.size + calculatedRules.size

        return accumulator
    }
}