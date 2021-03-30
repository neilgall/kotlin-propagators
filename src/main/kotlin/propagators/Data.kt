package propagators

typealias Contradiction = String

sealed class Content<out T> {
    object Empty : Content<Nothing>()
    data class Value<T>(val value: T) : Content<T>()
    data class Contradiction(val contradiction: propagators.Contradiction) : Content<Nothing>()

    final override fun toString(): String = when (this) {
        is Empty -> "{}"
        is Value<T> -> "{$value}"
        is Contradiction -> "contradiction($contradiction)"
    }
}

sealed class MergeResult<out T> {
    object Redundant: MergeResult<Nothing>()
    data class Value<T>(val value: T): MergeResult<T>()
    data class Contradiction(val contradiction: propagators.Contradiction): MergeResult<Nothing>()
}

interface Data<T> {
    fun T.merge(other: T): MergeResult<T>
}
