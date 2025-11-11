package ru.bmstu.naburnm8.adaptiveQueue.internal.rule

import ru.bmstu.naburnm8.adaptiveQueue.inner.entry.QueueEntry
import ru.bmstu.naburnm8.adaptiveQueue.inner.manager.PriorityRule

const val RULE_DOES_NOT_APPLY: Double = -1.0

class RuleManager<T> (
    private val rules: List<PriorityRule<T>>
) {
    fun calculateIfApplicable(entry: QueueEntry<T>): Double {
        for (rule in rules) {
            if (rule.condition(entry.model)) {
                return rule.calculate(entry.model)
            } else {
                return RULE_DOES_NOT_APPLY
            }
        }
        return RULE_DOES_NOT_APPLY
    }
}