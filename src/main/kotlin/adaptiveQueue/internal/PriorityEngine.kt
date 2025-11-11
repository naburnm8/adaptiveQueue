package ru.bmstu.naburnm8.adaptiveQueue.internal

import ru.bmstu.naburnm8.adaptiveQueue.inner.entry.QueueEntry
import adaptiveQueue.internal.rule.RuleManager

class PriorityEngine<T> (
    private val ruleManager: RuleManager<T>,
) {
    fun calculate(entry: QueueEntry<T>) {
        val calculated = entry.calculateActiveParams()

    }
}