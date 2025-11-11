package ru.bmstu.naburnm8.adaptiveQueue

import ru.bmstu.naburnm8.adaptiveQueue.event.EventIn
import ru.bmstu.naburnm8.adaptiveQueue.event.EventOut
import ru.bmstu.naburnm8.adaptiveQueue.event.PlacedQueueEntry
import ru.bmstu.naburnm8.adaptiveQueue.internal.entry.QueueEntry
import ru.bmstu.naburnm8.adaptiveQueue.internal.rule.PriorityRule
import ru.bmstu.naburnm8.adaptiveQueue.internal.PriorityEngine
import ru.bmstu.naburnm8.adaptiveQueue.internal.rule.RuleManager
import ru.bmstu.naburnm8.adaptiveQueue.internal.UpdateManager
import ru.bmstu.naburnm8.adaptiveQueue.internal.exception.ModelNotFoundException
import java.util.PriorityQueue

data class PrioritizedEntry<T> (
    val entry: T,
    val priority: Double
)

class AdaptiveQueue<T> (
    entries: List<QueueEntry<T>> = emptyList(),
    rules: List<PriorityRule<T>> = emptyList(),
) {
    val storage: PriorityQueue<PrioritizedEntry<QueueEntry<T>>> = PriorityQueue<PrioritizedEntry<QueueEntry<T>>>(compareByDescending { it.priority })
    val ruleManager: RuleManager<T> = RuleManager(ArrayList(rules))
    val priorityEngine: PriorityEngine<T> = PriorityEngine(ruleManager)
    val updateManager: UpdateManager<T> = UpdateManager(priorityEngine, ruleManager, storage)


    init {
        for (entry in entries) {
            val priority = priorityEngine.calculate(entry)
            storage.add(PrioritizedEntry(entry, priority))
        }
    }

    fun handleEvent(event: EventIn<T>): EventOut<T> {
        try {
            when (event) {
                is EventIn.CanTriggerReevaluation<T> -> return updateManager.handleEvent(event)
                is EventIn.PeekAll -> {
                    val sorted = storage.sortedByDescending { it.priority }
                    val placed = sorted.mapIndexed {index, e -> PlacedQueueEntry(e, index) }
                    return EventOut.PeekResponseAll(placed)

                }
                is EventIn.PeekOne -> {
                    val element = if (event.model == null) storage.peek() else storage.find { it.entry.model == event.model }
                    if (element == null) {
                        return EventOut.OperationNotCompleted(exception = ModelNotFoundException(event.model.toString()))
                    }
                    val place = storage.sortedByDescending { it.priority }.indexOf(element)
                    return EventOut.PeekResponseOne(PlacedQueueEntry(element, place))
                }
            }
        } catch (e: Exception) {
            return EventOut.OperationNotCompleted(exception = e)
        }
    }
}