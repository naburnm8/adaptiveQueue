package ru.bmstu.naburnm8.adaptiveQueue.internal.rule

import ru.bmstu.naburnm8.adaptiveQueue.internal.entry.QueueEntry


class RuleManager<T> (
    private val rules: List<PriorityRule<T>>
) {
    fun calculateRules(entry: QueueEntry<T>): List<Double> {
        val output = mutableListOf<Double>()
        for (rule in rules) {
            if (rule.condition(entry.model)) {
                var calculated = rule.calculate(entry.model)

                if (calculated > 1.0) {
                    calculated = 1.0
                } else if (calculated < 0.0) {
                    calculated = 0.0
                }

                output.add(calculated)
            }
        }
        return output
    }
}