package ru.bmstu.naburnm8.adaptiveQueue.inner.entry

class QueueEntry<T> (
    val model: T,
    private val params: List<QueueParam<T>>,
) {
    private val activeParams: MutableSet<QueueParam<T>> = HashSet(params.toMutableSet())

    fun calculateActiveParams(): List<CalculatedQueueParam> {
        val out = ArrayList<CalculatedQueueParam>()
        for (param in activeParams) {
            out.add(
                CalculatedQueueParam(
                    name = param.name,
                    value = param.compute(model)
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