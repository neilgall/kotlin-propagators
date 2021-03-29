package propagators

sealed class Content<out T> {
    object Empty : Content<Nothing>()
    data class Value<T>(val value: T) : Content<T>()
    data class Contradiction(val describe: String) : Content<Nothing>()

    final override fun toString(): String = when (this) {
        is Empty -> "{}"
        is Value<T> -> "{$value}"
        is Contradiction -> "contradiction($describe)"
    }
}

interface Data<T> {
    fun T.isRedundant(other: T): Boolean
    fun T.merge(other: T): Content<T>
}

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
            is Content.Value<T> -> data.run {
                if (!oldContent.value.isRedundant(newContent)) {
                    content = oldContent.value.merge(newContent)
                }
            }
            is Content.Contradiction -> {
                // nothing to do
            }
        }
    }
}
