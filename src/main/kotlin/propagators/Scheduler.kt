package propagators

import java.util.*

interface Scheduler {
    fun enqueue(p: Propagator)
}

class QueueScheduler: Scheduler {
    private val queue: Deque<Propagator> = ArrayDeque()

    override fun enqueue(p: Propagator) {
        queue.addLast(p)
    }

    fun run() {
        while (!queue.isEmpty()) {
            queue.removeFirst().invoke()
        }
    }
}

