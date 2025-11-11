package ru.bmstu.naburnm8.adaptiveQueue.internal.exception

import ru.bmstu.naburnm8.adaptiveQueue.event.RuleOperationType
import java.util.UUID


class RuleOperationException (ruleId: UUID, ruleOperation: RuleOperationType) : Exception("Rule operation failed: $ruleId, $ruleOperation")