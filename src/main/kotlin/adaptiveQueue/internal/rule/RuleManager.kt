package ru.bmstu.naburnm8.adaptiveQueue.internal.rule

import ru.bmstu.naburnm8.adaptiveQueue.internal.entry.QueueEntry
import java.util.UUID

enum class RuleOperationResult {
    Success,
    Failure
}

class RuleManager<T> (
    private val rules: ArrayList<PriorityRule<T>>
) {
    fun addRule(rule: PriorityRule<T>): RuleOperationResult {
        rules.add(rule)
        return RuleOperationResult.Success
    }
    fun updateRule(rule: PriorityRule<T>): RuleOperationResult {
        val found = rules.find {it.identifier == rule.identifier}
        if (found == null) {
            return RuleOperationResult.Failure
        }
        val index = rules.indexOf(found)
        rules[index] = rule
        return RuleOperationResult.Success
    }
    fun removeRule(ruleId: UUID): RuleOperationResult {
        val found = rules.find {it.identifier == ruleId}
        if (found == null) {
            return RuleOperationResult.Failure
        }
        val index = rules.indexOf(found)
        rules.removeAt(index)
        return RuleOperationResult.Success
    }
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