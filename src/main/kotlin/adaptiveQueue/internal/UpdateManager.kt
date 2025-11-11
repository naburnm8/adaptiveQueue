package ru.bmstu.naburnm8.adaptiveQueue.internal

import ru.bmstu.naburnm8.adaptiveQueue.PrioritizedEntry
import ru.bmstu.naburnm8.adaptiveQueue.event.EventIn
import ru.bmstu.naburnm8.adaptiveQueue.event.EventOut
import ru.bmstu.naburnm8.adaptiveQueue.event.ParameterManipulationType
import ru.bmstu.naburnm8.adaptiveQueue.event.RuleOperationType
import ru.bmstu.naburnm8.adaptiveQueue.internal.entry.QueueEntry
import ru.bmstu.naburnm8.adaptiveQueue.internal.exception.DequeuedFromEmptyQueueException
import ru.bmstu.naburnm8.adaptiveQueue.internal.exception.ModelNotFoundException
import ru.bmstu.naburnm8.adaptiveQueue.internal.exception.ParameterNotFoundException
import ru.bmstu.naburnm8.adaptiveQueue.internal.exception.RuleOperationException
import ru.bmstu.naburnm8.adaptiveQueue.internal.rule.PriorityRule
import ru.bmstu.naburnm8.adaptiveQueue.internal.rule.RuleManager
import ru.bmstu.naburnm8.adaptiveQueue.internal.rule.RuleOperationResult
import java.util.PriorityQueue

class UpdateManager<T> (
    private val priorityEngine: PriorityEngine<T>,
    private val ruleManager: RuleManager<T>,
    private val storage: PriorityQueue<PrioritizedEntry<QueueEntry<T>>>
) {
    fun handleEvent(event: EventIn.CanTriggerReevaluation<T>): EventOut<T> {
        val eventOut: EventOut<T>
        try {
            when (event) {
                is EventIn.CanTriggerReevaluation.Enqueue -> {
                    val priority = priorityEngine.calculate(event.entry)
                    storage.add(PrioritizedEntry(event.entry, priority))
                    eventOut = EventOut.Enqueued(event.entry.model)
                }

                is EventIn.CanTriggerReevaluation.Dequeue -> {
                    val dequeued = storage.poll()
                    eventOut = if (dequeued != null) {
                        EventOut.Dequeued(dequeued.entry.model)
                    } else {
                        EventOut.OperationNotCompleted(DequeuedFromEmptyQueueException())
                    }
                }

                is EventIn.CanTriggerReevaluation.DequeueByModel -> {
                    val iterator = storage.iterator()
                    var found: PrioritizedEntry<QueueEntry<T>>? = null
                    while (iterator.hasNext()) {
                        val next = iterator.next()
                        if (next.entry.model == event.model) {
                            found = next
                            iterator.remove()
                            break
                        }
                    }

                    eventOut = if (found != null) {
                        EventOut.Dequeued(found.entry.model)
                    } else {
                        EventOut.OperationNotCompleted(ModelNotFoundException(event.model.toString()))
                    }
                }

                is EventIn.CanTriggerReevaluation.AddRule -> {
                    val result = ruleManager.addRule(event.newRule)
                    eventOut = if (result == RuleOperationResult.Success) EventOut.RuleSuccess(event.newRule, RuleOperationType.Added)
                        else EventOut.OperationNotCompleted(RuleOperationException(event.newRule.identifier, RuleOperationType.Added))
                }

                is EventIn.CanTriggerReevaluation.UpdateRule -> {
                    val result = ruleManager.updateRule(event.updatedRule)
                    eventOut = if (result == RuleOperationResult.Success) EventOut.RuleSuccess(event.updatedRule, RuleOperationType.Updated)
                        else EventOut.OperationNotCompleted(RuleOperationException(event.updatedRule.identifier, RuleOperationType.Updated))
                }

                is EventIn.CanTriggerReevaluation.DeleteRule -> {
                    val result = ruleManager.removeRule(event.ruleIdToRemove)
                    eventOut = if (result == RuleOperationResult.Success) EventOut.RuleSuccess(PriorityRule(identifier = event.ruleIdToRemove, {false}, {0.0}), RuleOperationType.Removed)
                        else EventOut.OperationNotCompleted(RuleOperationException(event.ruleIdToRemove, RuleOperationType.Removed))
                }

                is EventIn.CanTriggerReevaluation.Reevaluate -> {
                    eventOut = EventOut.Reevaluated()
                }

                is EventIn.CanTriggerReevaluation.ReevaluateOne -> {
                    val found = storage.find {it.entry.model == event.model}
                    if (found != null) {
                        val newPriority = priorityEngine.calculate(found.entry)
                        storage.remove(found)
                        storage.add(found.copy(priority = newPriority))
                        eventOut = EventOut.Reevaluated()
                    } else {
                        eventOut = EventOut.OperationNotCompleted(ModelNotFoundException(event.model.toString()))
                    }
                }
                is EventIn.CanTriggerReevaluation.ToggleParameter -> {
                    val list = storage.toList()
                    var result: EventOut<T>? = null
                    for (it in list) {
                        val entry = it.entry
                        if (entry.hasParam(event.parameterName)) {
                            when (event.manipulationType) {
                                ParameterManipulationType.ON -> entry.activate(event.parameterName)
                                ParameterManipulationType.OFF -> entry.deactivate(event.parameterName)
                            }
                        } else {
                            result = EventOut.OperationNotCompleted(ParameterNotFoundException(entry.model.toString(), event.parameterName))
                            break
                        }
                    }
                    eventOut = result ?: EventOut.ParamsUpdated()
                }
            }
            if (event.forceReevaluate) {
                val updated = storage.map {
                    it.copy(priority = priorityEngine.calculate(it.entry))
                }
                storage.clear()
                storage.addAll(updated)
            }
            return eventOut
        } catch (e: Exception) {
            return EventOut.OperationNotCompleted(e)
        }
    }
}