package ru.bmstu.naburnm8.adaptiveQueue.internal.entry

class QueueEntry<T> (
    val model: T,
    private val params: List<QueueParam<T>>,
) {
    private val activeParams: MutableSet<QueueParam<T>> = HashSet(params.toMutableSet())

    fun calculateActiveParams(): List<CalculatedQueueParam> {
        val out = ArrayList<CalculatedQueueParam>()
        for (param in activeParams) {
            var computed = param.compute(model) * param.weight
            if (computed > 1.0) {
                computed = 1.0
            } else if (computed < 0.0) {
                computed = 0.0
            }
            out.add(
                CalculatedQueueParam(
                    name = param.name,
                    value = computed,
                )
            )
        }
        return out
    }

    fun deactivateAll() {
        activeParams.clear()
    }

    fun activateAll() {
        activeParams.clear()
        activeParams.addAll(params.toMutableList())
    }

    fun activate(qualifier: String) {
        for (param in params) {
            if (qualifier == param.name) {
                activeParams.add(param)
            }
        }
    }

    fun deactivate(qualifier: String) {
        var found: QueueParam<T>? = null

        for (param in activeParams) {
            if (qualifier == param.name) {
                found = param
                break
            }
        }

        if (found != null) {
            activeParams.remove(found)
        }
    }
}