import ru.bmstu.naburnm8.adaptiveQueue.AdaptiveQueue
import ru.bmstu.naburnm8.adaptiveQueue.event.EventIn
import ru.bmstu.naburnm8.adaptiveQueue.event.EventOut
import ru.bmstu.naburnm8.adaptiveQueue.internal.entry.QueueEntry
import ru.bmstu.naburnm8.adaptiveQueue.internal.entry.QueueParam
import ru.bmstu.naburnm8.adaptiveQueue.internal.rule.PriorityRule
import kotlin.test.Test
import kotlin.test.assertEquals

class EssentialsTest {
    private data class Student(
        val id: Int,
        val name: String,
    )

    private data class RequestFromStudent(
        val id: Int,
        val student: Student,
        val requestedTimeMinutes: Int,
        val amountOfDebts: Int,
    )

    private fun createRequests(): List<RequestFromStudent> {
        val out = listOf<RequestFromStudent>(
            RequestFromStudent(
                id = 1,
                student = Student(1, "Misha"),
                requestedTimeMinutes = 15,
                amountOfDebts = 4,
            ),
            RequestFromStudent(
                id = 2,
                student = Student(2, "Katya"),
                requestedTimeMinutes = 10,
                amountOfDebts = 4,
            ),
            RequestFromStudent(
                id = 3,
                student = Student(3, "Bob"),
                requestedTimeMinutes = 15,
                amountOfDebts = 0,
            )
        )
        return out
    }

    private fun createParams(): List<QueueParam<RequestFromStudent>> {
        val out = listOf<QueueParam<RequestFromStudent>>(
            QueueParam(
                "slotDurationCheck",
                0.5,
                compute = {
                    (15.0 / it.requestedTimeMinutes)
                }
            ),
            QueueParam(
                "amountOfDebtsCheck",
                1.0,
                compute = { it ->
                    if (it.amountOfDebts == 0) {
                        1.0
                    } else {
                        (1.0 / it.amountOfDebts)
                    }
                }
            )
        )
        return out
    }

    private fun createEntries(): List<QueueEntry<RequestFromStudent>> {
        val requests = createRequests()
        val entries = ArrayList<QueueEntry<RequestFromStudent>>()

        for (request in requests) {
            entries.add(QueueEntry(request, createParams()))
        }

        return entries
    }

    private fun createRules(): List<PriorityRule<RequestFromStudent>> {
        val out = listOf<PriorityRule<RequestFromStudent>>(
            PriorityRule(
                condition = { true },
                calculate = {
                    if (listOf("Katya", "Misha").contains(it.student.name)) {
                        1.0
                    } else {
                        0.0
                    }
                }
            ),
        )
        return out
    }

    @Test
    fun `check position and priority during enqueue`() {
        val entries = createEntries()
        val queue = AdaptiveQueue<RequestFromStudent>()
        queue.enqueue(entries[0])
        val mishaPriority = ((1 * 0.5) + (1.0 / 4)) / 2.0

        var peek = queue.handleEvent(EventIn.PeekAll()) as EventOut.PeekResponseAll<RequestFromStudent>

        assertEquals(mishaPriority, peek.entries[0].entry.priority)
        assertEquals(0, peek.entries[0].place)

        queue.enqueue(entries[1])

        val katyaPriority = ((1.5 * 0.5) + (1.0 / 4)) / 2.0

        val katyaPeek =
            queue.handleEvent(EventIn.PeekOne(entries[1].model)) as EventOut.PeekResponseOne<RequestFromStudent>

        assertEquals(katyaPriority, katyaPeek.entry.entry.priority)
        assertEquals(0, katyaPeek.entry.place)

        peek = queue.handleEvent(EventIn.PeekAll()) as EventOut.PeekResponseAll<RequestFromStudent>

        assertEquals(1, peek.entries[1].place) // Misha moved to second place


        /*
        for (entry in peek.entries) {
            println("Position ${entry.place}, priority ${entry.entry.priority}, model ${entry.entry.entry.model}")
        }
         */
    }

    @Test
    fun `check position during dequeue`() {
        val entries = createEntries()
        val queue = AdaptiveQueue(entries)

        queue.dequeue()

        var peek = queue.handleEvent(EventIn.PeekAll()) as EventOut.PeekResponseAll<RequestFromStudent>

        assertEquals("Katya", peek.entries[0].entry.entry.model.student.name) // Katya first after deque
        assertEquals("Misha", peek.entries[1].entry.entry.model.student.name) // Misha second

        queue.dequeueByModel(entries[0].model)

        peek = queue.handleEvent(EventIn.PeekAll()) as EventOut.PeekResponseAll<RequestFromStudent>

        assertEquals("Katya", peek.entries[0].entry.entry.model.student.name) // Katya first after second dequeue

    }

    @Test
    fun `check priority and position after applying a rule`() {
        val entries = createEntries()
        val queue = AdaptiveQueue(entries)

        var peek = queue.handleEvent(EventIn.PeekAll()) as EventOut.PeekResponseAll<RequestFromStudent>
        val mishaPriority = ((1 * 0.5) + (1.0 / 4)) / 2.0
        val katyaPriority = ((1.5 * 0.5) + (1.0 / 4)) / 2.0
        val bobPriority = ((0.5) + (1)) / 2.0

        //Check priorities before rule

        assertEquals(bobPriority, peek.entries[0].entry.priority)
        assertEquals(katyaPriority, peek.entries[1].entry.priority)
        assertEquals(mishaPriority, peek.entries[2].entry.priority)

        println("Before rule: ")
        for (entry in peek.entries) {
            println("Position ${entry.place}, priority ${entry.entry.priority}, model ${entry.entry.entry.model}")
        }

        queue.handleEvent(EventIn.CanTriggerReevaluation.AddRule(createRules()[0], true))

        peek = queue.handleEvent(EventIn.PeekAll()) as EventOut.PeekResponseAll<RequestFromStudent>

        val newKatyaPriority = ((1.5 * 0.5) + (1.0 / 4) + 1.0) / 3.0
        val newMishaPriority = ((1 * 0.5) + (1.0 / 4) + 1.0) / 3.0
        val newBobPriority = ((0.5) + (1) + 0) / 3.0

        assertEquals(newBobPriority, peek.entries[2].entry.priority)
        assertEquals(newKatyaPriority, peek.entries[0].entry.priority)
        assertEquals(newMishaPriority, peek.entries[1].entry.priority)

        println("After rule: ")
        for (entry in peek.entries) {
            println("Position ${entry.place}, priority ${entry.entry.priority}, model ${entry.entry.entry.model}")
        }
    }
}