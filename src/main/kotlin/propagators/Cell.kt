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
                when (val result = oldContent.value.merge(newContent)) {
                    is MergeResult.Redundant -> {
                        // do nothing
                    }
                    is MergeResult.Merged<T> -> {
                        content = Content.Value(result.value)
                    }
                    is MergeResult.Replaced<T> -> {
                        content = Content.Value(result.value)
                    }
                    is MergeResult.Contradiction -> {
                        content = Content.Contradiction(result.contradiction)
                    }
                }
            }
        }
    }
}

typealias CellFactory<T> = (String) -> Cell<T>

fun <T> Scheduler.cellFactory(data: Data<T>): CellFactory<T> = {
    name -> Cell(name, this, data)
}
