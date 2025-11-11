package ru.bmstu.naburnm8.adaptiveQueue

import ru.bmstu.naburnm8.adaptiveQueue.inner.entry.QueueEntry
import ru.bmstu.naburnm8.adaptiveQueue.inner.manager.PriorityRule
import ru.bmstu.naburnm8.adaptiveQueue.internal.PriorityEngine
import adaptiveQueue.internal.rule.RuleManager
import ru.bmstu.naburnm8.adaptiveQueue.internal.UpdateManager
import java.util.PriorityQueue

data class PrioritizedEntry<T> (
    val entry: T,
    val priority: Double
)

class AdaptiveQueue<T> (
    entries: List<QueueEntry<T>> = emptyList(),
    rules: List<PriorityRule<T>> = emptyList(),
) {
    val storage: PriorityQueue<PrioritizedEntry<T>> = PriorityQueue<PrioritizedEntry<T>>(compareByDescending { it.priority })
    val ruleManager: RuleManager<T> = RuleManager(rules)
    val priorityEngine: PriorityEngine<T> = PriorityEngine(ruleManager)
    val updateManager: UpdateManager<T> = UpdateManager(priorityEngine, storage)


    init {

    }
}