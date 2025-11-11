package ru.bmstu.naburnm8.adaptiveQueue.event

import ru.bmstu.naburnm8.adaptiveQueue.PrioritizedEntry
import ru.bmstu.naburnm8.adaptiveQueue.internal.entry.QueueEntry
import ru.bmstu.naburnm8.adaptiveQueue.internal.rule.PriorityRule

enum class RuleOperationType {
    Added,
    Removed,
    Updated,
}

data class PlacedQueueEntry<T> (
    val entry: PrioritizedEntry<QueueEntry<T>>,
    val place: Int
)

sealed class EventOut<T> {
    class Enqueued<T>(val model: T) : EventOut<T>()
    class Dequeued<T>(val model: T) : EventOut<T>()
    class RuleSuccess<T> (val rule: PriorityRule<T>, val operationType: RuleOperationType) : EventOut<T>()
    class PeekResponseOne<T> (val entry: PlacedQueueEntry<T>) : EventOut<T>()
    class PeekResponseAll<T> (val entries: List<PlacedQueueEntry<T>>) : EventOut<T>()
    class ParamsUpdated<T> : EventOut<T>()
    class OperationNotCompleted<T> (val exception: Exception) : EventOut<T>()
}