package ru.bmstu.naburnm8.adaptiveQueue.event

import ru.bmstu.naburnm8.adaptiveQueue.internal.entry.QueueEntry
import ru.bmstu.naburnm8.adaptiveQueue.internal.rule.PriorityRule
import java.util.UUID

enum class ParameterManipulationType{
    OFF,
    ON
}

sealed class EventIn<T> {
    sealed class CanTriggerReevaluation<T>(val forceReevaluate: Boolean) : EventIn<T>() {
        class Enqueue<T>(val entry: QueueEntry<T>, forceReevaluate: Boolean = false): CanTriggerReevaluation<T>(forceReevaluate)
        class Dequeue<T>(forceReevaluate: Boolean = false): CanTriggerReevaluation<T>(forceReevaluate)
        class DequeueByModel<T>(val model: T, forceReevaluate: Boolean = false) : CanTriggerReevaluation<T>(forceReevaluate)
        class AddRule<T>(val newRule: PriorityRule<T>, forceReevaluate: Boolean = false): CanTriggerReevaluation<T>(forceReevaluate)
        class UpdateRule<T>(val updatedRule: PriorityRule<T>, forceReevaluate: Boolean = false): CanTriggerReevaluation<T>(forceReevaluate)
        class DeleteRule<T>(val ruleIdToRemove: UUID, forceReevaluate: Boolean = false): CanTriggerReevaluation<T>(forceReevaluate)
        class Reevaluate<T> : CanTriggerReevaluation<T>(true)
        class ReevaluateOne<T> (val model: T, forceReevaluate: Boolean = false) : CanTriggerReevaluation<T>(forceReevaluate)
        class ToggleParameter (val parameterName: String, val manipulationType: ParameterManipulationType, forceReevaluate: Boolean = false) : CanTriggerReevaluation<Boolean>(forceReevaluate)
    }

    class PeekOne<T> (val model: T? = null) : EventIn<T>()
    class PeekAll<T> : EventIn<T>()
}