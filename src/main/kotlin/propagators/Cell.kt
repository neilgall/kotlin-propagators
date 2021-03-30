package propagators

class Cell<T>(private val name: String, private val scheduler: Scheduler, private val data: Data<T>) {

    private val neighbours: MutableSet<Propagator> = mutableSetOf()

    override fun toString(): String = "$name: $content"

    var content: Content<T> = Content.Empty
        private set(value) {
            field = value
            neighbours.forEach(scheduler::enqueue)
        }

    fun addNeighbour(neighbour: Propagator) {
        neighbours.add(neighbour)
        scheduler.enqueue(neighbour)
    }

    fun addContent(newContent: T) {
        when (val oldContent = content) {
            is Content.Empty -> {
                content = Content.Value(newContent)
            }
            is Content.Contradiction -> {
                // do nothing
            }
            is Content.Value<T> -> data.run {
                when (val merged = oldContent.value.merge(newContent)) {
                    is MergeResult.Redundant -> {
                        // do nothing
                    }
                    is MergeResult.Value<T> -> {
                        content = Content.Value(merged.value)
                    }
                    is MergeResult.Contradiction -> {
                        content = Content.Contradiction(merged.contradiction)
                    }
                }
            }
        }
    }
}
