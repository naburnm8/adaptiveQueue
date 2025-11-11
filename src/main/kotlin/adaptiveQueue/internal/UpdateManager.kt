package ru.bmstu.naburnm8.adaptiveQueue.internal

import ru.bmstu.naburnm8.adaptiveQueue.PrioritizedEntry
import java.util.PriorityQueue

class UpdateManager<T> (
    private val priorityEngine: PriorityEngine,
    private val storage: PriorityQueue<PrioritizedEntry<T>>
) {

}