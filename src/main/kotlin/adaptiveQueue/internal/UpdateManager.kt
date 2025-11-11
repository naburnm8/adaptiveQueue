package ru.bmstu.naburnm8.adaptiveQueue.internal

import ru.bmstu.naburnm8.adaptiveQueue.PrioritizedEntry
import ru.bmstu.naburnm8.adaptiveQueue.event.EventIn
import ru.bmstu.naburnm8.adaptiveQueue.event.EventOut
import ru.bmstu.naburnm8.adaptiveQueue.internal.entry.QueueEntry
import java.util.PriorityQueue

class UpdateManager<T> (
    private val priorityEngine: PriorityEngine<T>,
    private val storage: PriorityQueue<PrioritizedEntry<QueueEntry<T>>>
) {
    fun handleEvent(event: EventIn.CanTriggerReevaluation<T>): EventOut<T> {

    }
}