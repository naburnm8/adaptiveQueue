import ru.bmstu.naburnm8.adaptiveQueue.AdaptiveQueue
import ru.bmstu.naburnm8.adaptiveQueue.event.EventOut
import ru.bmstu.naburnm8.adaptiveQueue.internal.entry.QueueEntry
import ru.bmstu.naburnm8.adaptiveQueue.internal.entry.QueueParam
import kotlin.test.Test

class PerformanceTest {

    @Test
    fun `time complexity`() {
        val sizes = listOf(500, 1_000, 5_000, 10_000, 50_000, 100_000, 500_000, 1_000_000)
        for (n in sizes) {
            val entries = (1..n).map {
                QueueEntry(it, listOf(QueueParam("value", 1.0) { x -> x.toDouble() / n }))
            }
            val queue = AdaptiveQueue<Int>(entries = emptyList())

            val startEnqueue = System.nanoTime()
            for (entry in entries) queue.enqueue(entry)
            val endEnqueue = System.nanoTime()

            val startDequeue = System.nanoTime()
            while (true) {
                val result = queue.dequeue()
                if (result is EventOut.OperationNotCompleted<*>) break
            }
            val endDequeue = System.nanoTime()

            println(
                "N=$n: enqueue=${(endEnqueue - startEnqueue) / 1e6} ms, " +
                        "dequeue=${(endDequeue - startDequeue) / 1e6} ms"
            )
        }
    }

    @Test
    fun `memory complexity`() {
        data class Tester(val i: Int)

        val sizes = listOf(500, 1_000, 5_000, 10_000, 50_000, 100_000, 500_000, 1_000_000)
        for (n in sizes) {
            val entries = (1..n).map {
                QueueEntry(Tester(it), listOf(QueueParam("value", 1.0) { x -> x.i.toDouble() / n }))
            }

            val runtime = Runtime.getRuntime()

            System.gc()

            val before = runtime.totalMemory() - runtime.freeMemory()
            val queue = AdaptiveQueue(entries = entries)
            val after = runtime.totalMemory() - runtime.freeMemory()

            println("Memory used for $n entries: ${(after - before)/1024} KB")
        }
    }
}